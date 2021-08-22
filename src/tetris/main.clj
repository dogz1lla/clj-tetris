;; DONE add shifting of pieces horizontally upon pressing left/right
;; DONE fix a bug where collisions are not checked against frozen pieces when 
;;       shifting piece horizontally
;; TODO think how to rotate pieces
(ns tetris.main
  (:require [tetris.game-state :as gs]
            [tetris.utils :as u]
            [quil.core :as q]))

(def game-history (atom [(gs/init-game)]))

(defn update-history
  "Append a new state to the game history list."
  [new-state]
  (swap! game-history #(conj % new-state)))

;; drawing
(def lx 500)
(def ly 800)
(def width (:width (:bucket (first @game-history))))
(def height (:height (:bucket (first @game-history))))
(def max-idx (* height width))
(def all-slots (range max-idx))
(def a (min (quot lx width) (quot ly height)))
(def lattice (for [j (range height) i (range width)] [(* i a) (* j a)]))


(defn clear-screen
  "Fill the screen with a color."
  []
  (q/background 200))

(defn piece 
  "Draw a single unit piece."
  [x0 y0]
  (q/rect x0 y0 a a))

(defn setup
  "quil setup function."
  []
  (clear-screen)
  (q/frame-rate 10))

(defn draw-game-state
  "Vectorize the game state and draw unit pieces."
  [game-state lattice]
  (let [vectorized-state (u/vectorize-state game-state)
        pieces (u/filter-pieces vectorized-state lattice)]
    (doseq [[x0 y0] (remove nil? pieces)] (piece x0 y0))))

(defn update-game
  "Append game history vector until the game is over."
  []
  (if (gs/game-over? (last @game-history)) 
    nil
    (update-history (gs/step (last @game-history))))
  game-history)

(defn shift-piece
  "Function to be called upon pressing left/right arrow button."
  [dx]
  (let [new-state (gs/shift-piece (last @game-history) dx)]
    (update-history new-state)))

(defn draw
  "quil draw function."
  []
  ;; reload canvas
  (clear-screen)
  (q/fill 220 150 255)
  ;; capture relevant key presses
  (if (q/key-pressed?)
    (case (q/key-as-keyword)
      :left (shift-piece -1)
      :right (shift-piece 1)
      nil)
    nil)
  ;; draw the latest iteration of the game state
  (let [history (update-game)]
    (draw-game-state (last @history) lattice)))

(defn render-game []
  (q/defsketch tetris-animation
    :title "tetris"
    :settings #(q/smooth 2)
    :setup setup
    :draw draw
    :features [:keep-on-top]
    :size [lx ly]
    :on-close #(println (str "Closed! Number of moves: " (count @game-history)))))

;; DONE add shifting of pieces horizontally upon pressing left/right
;; DONE fix a bug where collisions are not checked against frozen pieces when 
;;       shifting piece horizontally
;; TODO fix new piece spawn logic (switch to random piece)
;; TODO think how to rotate pieces
;; TODO fix rotation in the gamma-piece
;; Ok so with rotation the problem is that rotating vanilla gamma piece is 
;; different from rotating a gamma piece that was rotated by say pi;
;; Need to introduce rotational degress of freedom.
;; [ 0 0] [1 0] [2 0] [ 0 1]
;; [-1 1] [0 1] [1 1] [-1 0]
;;
;; [2  2] [1  2] [ 0  2] [2 1]
;; [1 -1] [0 -1] [-1 -1] [1 0]
;; 
;; TODO rewrite piece logic so that it is centered around a unit vector 
;; attached to the piece, this way can encode orientation
(ns tetris.main
  (:require [tetris.game-state :as gs]
            [tetris.pieces :as p]
            #_[tetris.utils :as u]
            [quil.core :as q]))

(def game-history (atom [(gs/init-game)]))

(defn update-history
  "Append a new state to the game history vector."
  [new-state]
  (swap! game-history #(conj % new-state)))

(defn vectorize-state
  "Turn game state into a list of 0s and 1s."
  [game-state]
  (let [{:keys [piece bucket]} game-state
        {:keys [width height contents]} bucket
        pieces (p/parts piece)
        all-cells (reduce conj contents pieces)
        all-slots (range (* width height))
        lin-coord (fn [coord] (+ (* width (last coord)) (first coord)))
        linearized-state (into #{} (map lin-coord all-cells))]
    (for [i all-slots] (if (linearized-state i) 1 0))))

(defn filter-pieces
  "Take a vectorized state and only replace zeros with nil."
  [vectorized-state lattice]
  (map #(if (pos? %1) %2 nil) vectorized-state lattice))

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
  (let [vectorized-state (vectorize-state game-state)
        pieces (filter-pieces vectorized-state lattice)]
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

(defn rotate-piece
  "Function to be called upon pressing rotate key."
  []
  (let [new-state (gs/rotate-piece (last @game-history))]
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
      :space (rotate-piece)
      nil)
    nil)
  ;; draw the latest iteration of the game state
  ;; NOTE test mode here
  ;;(if (< (count @game-history) 50)
    (let [history (update-game)]
      (draw-game-state (last @history) lattice))
    (draw-game-state (last @game-history) lattice));;)

(defn render-game []
  (q/defsketch tetris-animation
    :title "tetris"
    :settings #(q/smooth 2)
    :setup setup
    :draw draw
    :features [:keep-on-top]
    :size [lx ly]
    :on-close #(println (str "Closed! Number of moves: " (count @game-history)))))

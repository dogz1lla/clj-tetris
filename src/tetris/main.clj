;; Need to redefine the time inside the game. Currently it is coupled to the 
;; quil update time (fps); the game should have it's own definition of time 
;; and quil will draw "snapshots" of the game state at equially spaced time 
;; intervals.
;; Lets say the game state has its own internal clock that ticks every tau 
;; seconds; and the drawing happens every t seconds and t > tau.
;; Note that t has to be sufficiently large so that when player presses the 
;; button it changes the rendered image so fast it is percieved as instant.
;;
;; Above approach wont work because there is only one loop that is allowed to
;; run and that is the one in quil sketch.
;; But we dont need the internal game time do we? The whole point of having it 
;; is to be able to change the game state "in between" the time ticks of the 
;; rendering, ie independent of the ticks; but that is automatically taken care
;; of because quil's key press events can happen anytime and then they will 
;; just change the external atom that holds the game.
(ns tetris.main
  (:require [tetris.game-state :as gs]
            [quil.core :as q]))

(def game-history (atom [(gs/init-game)]))

(defn update-history [new-state]
  (swap! game-history #(conj % new-state)))

;(defn run-game []
;  (while (not (gs/game-over? (last @game-history)))
;    (update-history (gs/step (last @game-history))))
;  @game-history)

#_(defn render-game [nx ny game-history]
  (let [width (:width (:bucket (first @game-history)))
        height (:height (:bucket (first @game-history)))
        max-idx (* height width)
        all-slots (range max-idx)
        a (min (quot nx width) (quot ny height))
        lattice (for [j (range height) i (range width)] [(* i a) (* j a)])]))

;; drawing
(def lx 500)
(def ly 800)
(def width (:width (:bucket (first @game-history))))
(def height (:height (:bucket (first @game-history))))
(def max-idx (* height width))
(def all-slots (range max-idx))
(def box {:lx lx :ly ly})
(def a (min (quot lx width) (quot ly height)))

(defn vectorize-state
  "Turn game state in a list of 0s and 1s."
  [game-state]
  (let [{:keys [piece bucket]} game-state
        pieces (vals piece)
        bucket-contents (:contents bucket)
        all-cells (reduce conj bucket-contents pieces)
        linearized-state (into #{} (map #(+ (* width (last %)) (first %)) all-cells))]
    (for [i all-slots] (if (linearized-state i) 1 0))))

(defn lattice
  []
  (for [j (range height) i (range width)] [(* i a) (* j a)]))

(defn clear-screen
  []
  (q/background 200))

(defn piece 
  [x0 y0]
  (q/rect x0 y0 a a))

(defn setup
  []
  (clear-screen)
  (q/frame-rate 20)
  (println (str "Number of moves: " (count @game-history))))

(defn filter-pieces
  [vectorized-state lattice]
  (map #(if (pos? %1) %2 nil) vectorized-state lattice))

(defn draw-game-state
  [game-state lattice]
  (let [vectorized-state (vectorize-state game-state)
        pieces (filter-pieces vectorized-state lattice)]
    (doseq [[x0 y0] (remove nil? pieces)] (piece x0 y0))))

(defn update-game
  []
  (if (gs/game-over? (last @game-history)) 
    nil
    (update-history (gs/step (last @game-history))))
  game-history)

(defn shift-piece [direction]
  (let [dx (* direction a)
        new-state (gs/shift-piece (last @game-history) dx)]
    (update-history new-state)))

(defn draw
  []
  (clear-screen)
  (q/fill 220 150 255)
  (if (q/key-pressed?) (println (str (q/key-as-keyword) " key pressed!")) nil)
  (let [history (update-game)
        lattice (lattice)]
    (draw-game-state (last @history) lattice)))

(q/defsketch tetris-animation
  :title "tetris"
  :settings #(q/smooth 2)
  :setup setup
  :draw draw
  :features [:keep-on-top]
  :size [(:lx box) (:ly box)]
  :on-close #(println (str "closed! " (count (last (last @game-history))))))

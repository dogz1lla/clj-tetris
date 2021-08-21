(ns tetris.new-main
  (:require [tetris.game-state :as gs]
            [clojure.set :as cs]
            [quil.core :as q]))

(def game-history (atom [(gs/init-game)]))

;(defn run-game []
;  (while (not (gs/game-over? (last @game-history)))
;    (swap! game-history #(conj % (gs/step (last @game-history)))))
;  @game-history)

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
    (swap! game-history #(conj % (gs/step (last @game-history)))))
  game-history)

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

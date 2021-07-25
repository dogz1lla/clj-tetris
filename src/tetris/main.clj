(ns tetris.main
  (:require [tetris.pieces :as p]
            [clojure.set :as cs]
            [quil.core :as q]))

;; Pieces: 
;; xx  xxx  xxx   xx  xx    x   xxxx
;; xx    x  x    xx    xx  xxx
;; 
;; each piece has 4 orientations 
;; play field is a grid of lattice points; each point can be occupied or free
;; once a row of the points is fully occupied it dissappears
;;
;; if the cells are described by numbers from 1 to n;
;; then one can define the pieces for each of their orientations
;; 
;; i need to pick a "reference point" for each of the pieces 
;; ie the point that describes its position
;;
;; [1 0 0 0 0 0 0 0]
;; [0 0 1 0 0 0 0 0]
;; [0 0 0 0 1 0 0 0]
;; [0 0 0 0 0 0 1 0]
;;
;; 1. init the playing field: a piece at the top of the bucket (random x) and 
;;    (optionally) pieces at the bottom;
;; 2. on each step check if the piece is sinked; if it is spawn a new one (ie 
;;    reset idx and create a 1 in that spot); if it isnt then move the piece; 
;; 3. add the new state to the game atom;
;;
;; DONE: create a test run using the bucket and a single piece (just a 
;;       single square); most likely need to move bucket into an atom.
;; DONE: add several pieces to the bucket; add collision (pieces stack)
;; DONE: push side effects (interacting with state) to the boundaries
;; DONE: write logic to visualize the game state
;; DONE: use quil to animate the game run
;; DONE: rewrite the unit piece logic in a way that is easier extendable to 
;;       non-trivial pieces; implementation: replaced plain integer with set
;;       as the first value of the game state vector
;; DONE: write logic for pieces of non-trivial shapes
;; DONE: need to use 2d coordinates after all (for wall overlap checks)
;; DONE: prevent wall overlaps on piece spawn
;; TODO: think how to formulate game-over in terms of empty set of possible 
;;       moves

;(defn complete-row? [row]
;  (every? true? row))

(def width 20)
(def height 30)
(def max-idx (* height width))
(def all-slots (range max-idx))

(defn pick-x [] 
  (rand-int width))

(def empty-bucket #{})
;;(def init-state [(p/single-piece (pick-x) 0) empty-bucket])
(def init-state [(p/square-piece (pick-x) 0 width) empty-bucket])
(def game-history (atom [init-state]))

(defn fall 
  [piece]
  (assoc piece 1 (inc (last piece))))

(defn move-piece 
  "Make the piece fall by one unit towards bottom."
  [game-state]
  (let [pieces (first game-state)]
    (assoc game-state 0 (into #{} (map fall pieces)))))

(defn occupied? 
  "Check if the next space in piece's way is already taken."
  [new-positions field]
  (not-every? false? (map #(contains? field %) new-positions)))

(defn piece-at-bottom? 
  "Check if the piece reached the bottom of the bucket."
  [pieces]
  (not-every? true? (map #(< (last %) (dec height)) pieces)))

(defn piece-sinked? 
  "Check if the piece is 'sinked' i.e., it is either at the bottom or 
  the next space is occupied."
  [game-state]
  (let [[pieces field] game-state
        piece-shift (into #{} (map fall pieces))]
    (or (occupied? piece-shift field)
        (piece-at-bottom? pieces))))

(defn freeze-piece 
  "When piece is sinked it becomes a part of the static content of the bucket."
  [game-state]
  (let [[piece field] game-state]
    (assoc game-state 1 (cs/union field piece))))

(defn spawn-random-piece
  []
  (apply (rand-nth [p/square-piece 
                    p/gamma-piece
                    p/gamma-piece-mirror
                    p/tau-piece
                    p/sausage-piece
                    p/step-piece
                    p/step-piece-mirror]) (list (pick-x) 0 width)))

(defn spawn-piece 
  "Spawn a new piece at the top of the bucket."
  [game-state]
  ;;(let [new-piece (p/single-piece (pick-x) 0)]
  ;;(let [new-piece (p/square-piece (pick-x) 0 width)]
  ;;(let [new-piece (p/gamma-piece (pick-x) 0 width)]
  ;;(let [new-piece (p/gamma-piece-mirror (pick-x) 0 width)]
  ;;(let [new-piece (p/sausage-piece (pick-x) 0 width)]
  ;;(let [new-piece (p/step-piece (pick-x) 0 width)]
  ;;(let [new-piece (p/step-piece-mirror (pick-x) 0 width)]
  ;;(let [new-piece (p/tau-piece (pick-x) 0 width)]
  (let [new-piece (spawn-random-piece)]
    (assoc game-state 0 new-piece)))

(defn bucket-clogged?
  [game-state]
  (let [[_ frozen-pieces] game-state] 
    (not-every? false? (map #(zero? (last %)) frozen-pieces))))

(defn game-over? 
  "Game ends when there are no free places anymore."
  [game-state]
  ;;(empty? (cs/difference (set all-slots) (last game-state))))
  ;;(<= max-idx (count (last game-state))))
  ;;(<= 100 (count (last game-state))))
  (bucket-clogged? game-state))

(defn game-step 
  "Advance the game by one step. 
  1. check if the piece is sinked; 
  2. if it is then freeze it and spawn a new one;
  3. else advance it by one unit."
  [game-state]
  (if (piece-sinked? game-state)
    (-> game-state freeze-piece spawn-piece)
    (move-piece game-state)))

(defn vectorize-state
  "Turn game state in a list of 0s and 1s."
  [game-state]
  (let [[piece field] game-state
        all-cells (cs/union field piece)
        linearized-state (into #{} (map #(+ (* width (last %)) (first %)) all-cells))]
    (for [i all-slots] (if (linearized-state i) 1 0))))

(defn visualize-row
  "Turn a row of the vectorized state into a string of Xs and Os."
  [row]
  (reduce #(str %1 (if (pos? %2) "x" "o")) "" row))

(defn print-state 
  "Print every row of the game state as a string of Xs and Os."
  [vectorized-state]
  (let [rows (partition width vectorized-state)]
    (doseq [row rows]
      (println (visualize-row row))))
  (println))

(defn run-game 
  "Run the game until game over."
  []
  (while (not (game-over? (last @game-history)))
    #_(Thread/sleep 1000)
    #_(print-state (vectorize-state (last @game-history)))
    #_(println (last @game-history))
    (swap! game-history #(conj % (game-step (last @game-history)))))
  @game-history)

;; ============================================================================
;; plotting
;; ============================================================================
(def lx 500)
(def ly 800)
(def box {:lx lx :ly ly})
(def a (min (quot lx width) (quot ly height)))

(defn lattice
  []
  (for [j (range height) i (range width)] [(* i a) (* j a)]))

(defn clear-screen
  []
  (q/background 200))

(defn setup
  []
  (clear-screen)
  (q/frame-rate 200)
  (run-game)
  (println (str "Number of moves: " (count @game-history))))

(defn piece 
  [x0 y0]
  (q/rect x0 y0 a a))

(defn filter-pieces
  [vectorized-state lattice]
  (map #(if (pos? %1) %2 nil) vectorized-state lattice))

(defn draw-game-state
  [game-state lattice]
  (let [vectorized-state (vectorize-state game-state)
        pieces (filter-pieces vectorized-state lattice)]
    (doseq [[x0 y0] (remove nil? pieces)] (piece x0 y0))))

(def current-step (atom 0))

(defn make-step
  []
  (let [i @current-step]
    (swap! current-step inc)
    (if (< i (count @game-history)) (nth @game-history i) (last @game-history))))

(defn draw
  []
  (clear-screen)
  (q/fill 220 100 255)
  (let [state (make-step)
        lattice (lattice)]
    #_(Thread/sleep 1000)
    (draw-game-state state lattice)))

(q/defsketch tetris-animation
  :title "tetris"
  :settings #(q/smooth 2)
  :setup setup
  :draw draw
  :features [:keep-on-top]
  :size [(:lx box) (:ly box)]
  :on-close #(println (str "closed! " (count (last (last @game-history))))))

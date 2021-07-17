(ns tetris.main
  (:require [clojure.set :as cs]
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
;; TODO: write logic for pieces of non-trivial shapes

;(defn complete-row? [row]
;  (every? true? row))

(def width 10)
(def height 30)
(def max-idx (* height width))
(def all-slots (range max-idx))

(defn pick-x [] 
  (rand-int width))

(def empty-bucket #{})
(def init-state [(pick-x) empty-bucket])
(def game-history (atom [init-state]))

(defn move-piece 
  "Make the piece fall by one unit towards bottom."
  [game-state]
  (let [current-idx (first game-state)]
    (assoc game-state 0 (+ width current-idx))))

(defn occupied? 
  "Check if the next space in piece's way is already taken."
  [new-idx field]
  (contains? field new-idx))

(defn piece-at-bottom? 
  "Check if the piece reached the bottom of the bucket."
  [idx]
  (<= (- max-idx idx) width))

(defn piece-sinked? 
  "Check if the piece is 'sinked' i.e., it is either at the bottom or 
  the next space is occupied."
  [game-state]
  (let [[idx field] game-state]
    (or (occupied? (+ idx width) field)
        (piece-at-bottom? idx))))

(defn freeze-piece 
  "When piece is sinked it becomes a part of the static content of the bucket."
  [game-state]
  (let [[idx field] game-state]
    (assoc game-state 1 (conj field idx))))

(defn spawn-piece 
  "Spawn a new piece at the top of the bucket."
  [game-state]
  (let [new-idx (pick-x)]
    (assoc game-state 0 new-idx)))

(defn game-over? 
  "Game ends when there are no free places anymore."
  [game-state]
  (empty? (cs/difference (set all-slots) (last game-state))))

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
  (let [[idx field] game-state
        pieces (conj field idx)]
    (for [i all-slots] (if (pieces i) 1 0))))

(defn visualize-row
  "Turn a row of the vectorized state into a string of Xs and Os."
  [row]
  (reduce #(str %1 (if (pos? %2) "x" "o")) "" row))

(defn print-state 
  "Print every row of the game state as a string of Xs and Os."
  [vectorized-state]
  (let [rows (partition width vectorized-state)]
    (doseq [row rows]
      #_(println row)
      (println (visualize-row row))))
  (println))

(defn run-game 
  "Run the game until game over."
  []
  (while (not (game-over? (last @game-history)))
    #_(Thread/sleep 1000)
    #_(print-state (vectorize-state (last @game-history)))
    (swap! game-history #(conj % (game-step (last @game-history)))))
  @game-history)

;; ============================================================================
;; plotting
;; ============================================================================
(def box {:lx 500 :ly 800})
(def lx 500)
(def ly 800)
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
  (q/frame-rate 1000)
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
  :on-close #(println "closed!"))

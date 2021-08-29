(ns tetris.utils)

(defn vectorize-state
  "Turn game state into a list of 0s and 1s."
  [game-state]
  (let [{:keys [piece bucket]} game-state
        {:keys [width height contents]} bucket
        pieces (vals piece)
        all-cells (reduce conj contents pieces)
        all-slots (range (* width height))
        lin-coord (fn [coord] (+ (* width (last coord)) (first coord)))
        linearized-state (into #{} (map lin-coord all-cells))]
    (for [i all-slots] (if (linearized-state i) 1 0))))

(defn filter-pieces
  "Take a vectorized state and only replace zeros with nil."
  [vectorized-state lattice]
  (map #(if (pos? %1) %2 nil) vectorized-state lattice))

(defn rightmost
  "Go over all of piece's parts and find the x-coord of the rightmost."
  [parts]
  (apply max (map first parts)))

(defn leftmost
  "Go over all of piece's parts and find the x-coord of the leftmost."
  [parts]
  (apply min (map first parts)))

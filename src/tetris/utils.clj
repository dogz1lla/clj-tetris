(ns tetris.utils)

(defn rightmost
  "Go over all of piece's parts and find the x-coord of the rightmost."
  [parts]
  (apply max (map first parts)))

(defn leftmost
  "Go over all of piece's parts and find the x-coord of the leftmost."
  [parts]
  (apply min (map first parts)))

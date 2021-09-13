(ns tetris.bucket)


;; ============================================================================
;; Bucket protocol
;; ============================================================================
(defprotocol BucketProtocol
  "A bucket protocol."
  (full-rows [this]
    "Return a collection of y-coordinates of rows that are full.")
  (erase-row [this ys]
    "Erase a row of sub-pieces with y-coordinate y.")
  (avalanche [this ys]
    "Shift all rows with y-coord > y in ys by one unit down.")
  (overflown? [this] 
    "Check if the bucket is overflown."))

(defn drop-rows 
  [contents y]
  (mapv #(if (> y (last %)) [(first %) (inc (last %))] %) contents))

(defrecord Bucket [width height contents]
  BucketProtocol

  (full-rows
    [this]
    (let [{:keys [width contents]} this]
      (->> contents
           (map last)
           (frequencies)
           (filter #(= width (last %)))
           (map first))))

  (avalanche 
    [this ys]
    (let [{:keys [contents]} this
          new-contents (reduce #(drop-rows %1 %2) contents ys)]
      (assoc this :contents new-contents)))
  
  (erase-row 
    [this ys]
    (let [{:keys [contents]} this
          filtered (filterv #(not (contains? (set ys) (last %))) contents)]
      (assoc this :contents filtered)))

  (overflown?
    [this] 
    (let [{:keys [_ bucket]} this
          bucket-contents (:contents bucket)]
      (not-every? false? (map #(zero? (last %)) bucket-contents)))))

(defn init-bucket [width height]
  (->Bucket width height []))

(ns tetris.bucket)


;; ============================================================================
;; Bucket protocol
;; ============================================================================
(defprotocol BucketProtocol
  "A bucket protocol."
  (overflown? [this] 
    "Check if the bucket is overflown."))

(defrecord Bucket [width height contents]
  BucketProtocol

  (overflown? [this] 
    (let [{:keys [_ bucket]} this
          bucket-contents (:contents bucket)]
      (not-every? false? (map #(zero? (last %)) bucket-contents)))))

(defn init-bucket [width height]
  (->Bucket width height []))

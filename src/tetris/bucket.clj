(ns tetris.bucket)


;; ============================================================================
;; Bucket protocol
;; ============================================================================
(defprotocol BucketProtocol
  "A bucket protocol."
  #_(spawn [this w h] 
    "Init the bucket.")
  (clogged? [this]))

(defrecord Bucket [width height contents]
  BucketProtocol
  (clogged? [this] false))

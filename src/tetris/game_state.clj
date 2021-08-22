(ns tetris.game-state
  (:require [tetris.pieces :as p]
            [tetris.bucket :as b]))


(def test-counter (atom 0))
;; ============================================================================
;; Game state protocol
;; ============================================================================
(defprotocol GameState
  "Game state."
  (piece-at-bottom? [this] 
    "Check if the piece reached the bottom of the bucket.")
  (occupied? [this new-piece-position]
    "Check if the next space in piece's way is already taken.")
  (piece-sinked? [this]
    "Check if the piece is 'sinked' i.e., it is either at the bottom or 
    the next space is occupied.")
  (freeze-piece [this]
    "When piece is sinked it becomes a part of bucket contents.")
  (choose-next-piece [this]
    "Choose how to spawn a new piece.")
  (move-piece [this]
    "Drop the piece by one unit.")
  (spawn-piece [this]
    "Spawn a new piece at the top of the bucket.")
  (step [this]
    "Make a step of the game.")
  (shift-piece [this dx]
    "Shift a piece.")
  (game-over? [this]
    "Check if game reached its end."))


;; ============================================================================
;; Game
;; ============================================================================
(defrecord Tetris [piece bucket]
  GameState

  (occupied? [this new-piece-position]
    (let [{:keys [_ bucket]} this
          bucket-contents (:contents bucket)
          bucket-set (set bucket-contents)
          positions (vals new-piece-position)]
      (not-every? false? (map #(contains? bucket-set %) positions))))
  
  (piece-at-bottom? [this]
    (let [{:keys [piece bucket]} this
          height (:height bucket)
          pieces (vals piece)]
      (not-every? true? (map #(< (last %) (dec height)) pieces))))

  (piece-sinked? [this]
    (let [{:keys [piece _]} this
          fallen-piece (p/fall piece)]
      (or (occupied? this fallen-piece) (piece-at-bottom? this))))

  (freeze-piece [this]
    (let [{:keys [piece bucket]} this
          {:keys [width height contents]} bucket
          pieces (vals piece)
          bucket-contents contents
          new-bucket-contents (reduce conj bucket-contents pieces)
          new-bucket (tetris.bucket.Bucket. width height new-bucket-contents)
          nil-piece (tetris.pieces.SquarePiece. nil nil nil nil)]
      (Tetris. nil-piece new-bucket)))

  (choose-next-piece [this]
    (let [{:keys [_ bucket]} this
          {:keys [width _ _]} bucket]
      (p/square-piece 0 0 width)))

  (spawn-piece [this] 
    (let [new-piece (choose-next-piece this)]
      (assoc this :piece new-piece)))

  (move-piece [this] 
    (let [{:keys [piece _]} this
          new-piece (p/fall piece)]
      (assoc this :piece new-piece)))

  (step [this]
    (if (piece-sinked? this)
      (-> this freeze-piece spawn-piece)
      (move-piece this)))
  
  (shift-piece [this dx] 
    (let [{:keys [piece _]} this
          {:keys [width _ _]} bucket
          new-piece (p/shift piece dx width)]
      (assoc this :piece new-piece)))

  (game-over? [this]
    (b/overflown? (:bucket this))))

;; ============================================================================
;; Game init
;; ============================================================================
(defn init-game []
  (let[piece (p/square-piece 2 0 5)
       bucket (b/init-bucket 10 21)]
    (->Tetris piece bucket)))

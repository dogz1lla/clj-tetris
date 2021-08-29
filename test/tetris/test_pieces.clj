(ns tetris.test-pieces
  (:require [tetris.pieces :as p]
            [clojure.test :as t]))


(t/deftest test-init
  (t/testing "gamma piece"

    (t/testing "init"
      (let [[x0 y0] [0 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            {:keys [p0 p1 p2 p3]} test-piece]
        (t/is (= p0 [0 0]))
        (t/is (= p1 [1 0]))
        (t/is (= p2 [2 0]))
        (t/is (= p3 [0 1]))))

    (t/testing "wall collisions"
      (let [[x0 y0] [1 0]
            width 3
            test-piece (p/gamma-piece x0 y0 width)
            {:keys [p0 p1 p2 p3]} test-piece]
        (t/is (= p0 [0 0]))
        (t/is (= p1 [1 0]))
        (t/is (= p2 [2 0]))
        (t/is (= p3 [0 1]))))

    (t/testing "fall"
      (let [[x0 y0] [0 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            fallen-piece (p/fall test-piece)
            {:keys [p0 p1 p2 p3]} fallen-piece]
        (t/is (= p0 [0 1]))
        (t/is (= p1 [1 1]))
        (t/is (= p2 [2 1]))
        (t/is (= p3 [0 2]))))
    
    (t/testing "shift"
      (let [[x0 y0] [0 0]
            width 5
            dx 1
            test-piece (p/gamma-piece x0 y0 width)
            shifted-piece (p/shift test-piece dx width)
            {:keys [p0 p1 p2 p3]} shifted-piece]
        (t/is (= p0 [1 0]))
        (t/is (= p1 [2 0]))
        (t/is (= p2 [3 0]))
        (t/is (= p3 [1 1]))))
    
    (t/testing "negative shift"
      (let [[x0 y0] [1 0]
            width 5
            dx -1
            test-piece (p/gamma-piece x0 y0 width)
            shifted-piece (p/shift test-piece dx width)
            {:keys [p0 p1 p2 p3]} shifted-piece]
        (t/is (= p0 [0 0]))
        (t/is (= p1 [1 0]))
        (t/is (= p2 [2 0]))
        (t/is (= p3 [0 1]))))
    
    (t/testing "negative shift with collision"
      (let [[x0 y0] [0 0]
            width 5
            dx -1
            test-piece (p/gamma-piece x0 y0 width)
            shifted-piece (p/shift test-piece dx width)
            {:keys [p0 p1 p2 p3]} shifted-piece]
        (t/is (= p0 [0 0]))
        (t/is (= p1 [1 0]))
        (t/is (= p2 [2 0]))
        (t/is (= p3 [0 1]))))
    
    (t/testing "single rotation"
      (let [[x0 y0] [0 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            rotated-piece (p/rotate test-piece)
            {:keys [p0 p1 p2 p3]} rotated-piece]
        (t/is (= p0 [2 0]))
        (t/is (= p1 [2 1]))
        (t/is (= p2 [2 2]))
        (t/is (= p3 [1 0]))))
    
    (t/testing "rotate twice"
      (let [[x0 y0] [0 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            rotated-piece (p/rotate (p/rotate test-piece))
            {:keys [p0 p1 p2 p3]} rotated-piece]
        (t/is (= p0 [2 2]))
        (t/is (= p1 [1 2]))
        (t/is (= p2 [0 2]))
        (t/is (= p3 [2 1]))))
    
    (t/testing "rotations 3"
      (let [[x0 y0] [1 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            rotated-piece (p/rotate test-piece)
            {:keys [p0 p1 p2 p3]} rotated-piece]
        (t/is (= p0 [3 0]))
        (t/is (= p1 [3 1]))
        (t/is (= p2 [3 2]))
        (t/is (= p3 [2 0]))))))

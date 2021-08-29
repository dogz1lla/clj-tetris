(ns tetris.test-pieces
  (:require [clojure.test :as t]
            [tetris.pieces :as p]))


(t/deftest test-init
  (t/testing "gamma piece"

    (t/testing "init"
      (let [[x0 y0] [0 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            [p0 p1 p2 p3] (p/parts test-piece)]
        (t/is (= p0 [0 0]))
        (t/is (= p1 [1 0]))
        (t/is (= p2 [2 0]))
        (t/is (= p3 [0 1]))))

    (t/testing "fall"
      (let [[x0 y0] [0 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            fallen-piece (p/fall test-piece 1)
            [p0 p1 p2 p3] (p/parts fallen-piece)]
        (t/is (= p0 [0 1]))
        (t/is (= p1 [1 1]))
        (t/is (= p2 [2 1]))
        (t/is (= p3 [0 2]))))
    
    (t/testing "shift"
      (let [[x0 y0] [0 0]
            width 5
            dx 1
            test-piece (p/gamma-piece x0 y0 width)
            shifted-piece (p/shift test-piece dx)
            [p0 p1 p2 p3] (p/parts shifted-piece)]
        (t/is (= p0 [1 0]))
        (t/is (= p1 [2 0]))
        (t/is (= p2 [3 0]))
        (t/is (= p3 [1 1]))))
    
    (t/testing "negative shift"
      (let [[x0 y0] [0 0]
            width 5
            dx -1
            test-piece (p/gamma-piece x0 y0 width)
            shifted-piece (p/shift test-piece dx)
            [p0 p1 p2 p3] (p/parts shifted-piece)]
        (t/is (= p0 [-1 0]))
        (t/is (= p1 [0 0]))
        (t/is (= p2 [1 0]))
        (t/is (= p3 [-1 1]))))
    
    (t/testing "single rotation"
      (let [[x0 y0] [0 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            rotated-piece (p/rotate test-piece)
            [p0 p1 p2 p3] (p/parts rotated-piece)]
        (t/is (= p0 [1 0]))
        (t/is (= p1 [1 1]))
        (t/is (= p2 [1 2]))
        (t/is (= p3 [0 0]))))
    
    (t/testing "rotate twice"
      (let [[x0 y0] [0 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            rotated-piece (p/rotate (p/rotate test-piece))
            [p0 p1 p2 p3] (p/parts rotated-piece)]
        (t/is (= p0 [2 1]))
        (t/is (= p1 [1 1]))
        (t/is (= p2 [0 1]))
        (t/is (= p3 [2 0]))))
    
    (t/testing "rotate 3 times"
      (let [[x0 y0] [0 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            rotated-piece (p/rotate (p/rotate (p/rotate test-piece)))
            [p0 p1 p2 p3] (p/parts rotated-piece)]
        (t/is (= p0 [0 2]))
        (t/is (= p1 [0 1]))
        (t/is (= p2 [0 0]))
        (t/is (= p3 [1 2]))))

    (t/testing "rotate 4 times"
      (let [[x0 y0] [0 0]
            width 5
            test-piece (p/gamma-piece x0 y0 width)
            rotated-piece (p/rotate (p/rotate (p/rotate (p/rotate test-piece))))
            [p0 p1 p2 p3] (p/parts rotated-piece)]
        (t/is (= p0 [0 0]))
        (t/is (= p1 [1 0]))
        (t/is (= p2 [2 0]))
        (t/is (= p3 [0 1]))))
    
    (t/testing "left wall collision"
      (let [[x0 y0] [0 0]
            width 5
            dx -1
            test-piece (p/gamma-piece x0 y0 width)
            shifted-piece (p/shift test-piece dx)
            shifted-piece (p/check-left-wall-collision shifted-piece)
            [p0 p1 p2 p3] (p/parts shifted-piece)]
        (t/is (= p0 [0 0]))
        (t/is (= p1 [1 0]))
        (t/is (= p2 [2 0]))
        (t/is (= p3 [0 1]))))
    
    (t/testing "right wall collision"
      (let [[x0 y0] [0 0]
            width 5
            dx 4
            test-piece (p/gamma-piece x0 y0 width)
            shifted-piece (p/shift test-piece dx)
            shifted-piece (p/check-right-wall-collision shifted-piece width)
            [p0 p1 p2 p3] (p/parts shifted-piece)]
        (t/is (= p0 [2 0]))
        (t/is (= p1 [3 0]))
        (t/is (= p2 [4 0]))
        (t/is (= p3 [2 1]))))))

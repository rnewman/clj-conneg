(use 'clojure.test)
(use 'com.twinql.clojure.conneg)
(deftest test-acceptable-type
         (are [x y out] (= out (acceptable-type x y))
              ["text" "plain"]
              ["text" "plain"]
              ["text" "plain"]
              
              ["text" "*"]
              ["text" "*"]
              ["text" "*"]
              
              ["*" "*"]
              ["text" "plain"]
              ["text" "plain"]
              
              ["text" "*"]
              ["text" "plain"]
              ["text" "plain"]
              
              ["text" "plain"]
              ["text" "*"]
              ["text" "plain"]
              
              ["*" "*"]
              ["*" "*"]
              ["*" "*"]
              
              ["text" "plain"]
              ["image" "*"]
              nil))

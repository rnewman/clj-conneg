(ns com.twinql.clojure.conneg.test
  (:require [clojure.test :refer :all]
            [com.twinql.clojure.conneg :refer :all]))

(deftest test-acceptable-type
  (are [x y out] (= out (acceptable-type x y))
       ["text" "plain"] ["text" "plain"] ["text" "plain"]
       ["text" "*"]     ["text" "*"]     ["text" "*"]
       ["*" "*"]        ["text" "plain"] ["text" "plain"]
       ["text" "*"]     ["text" "plain"] ["text" "plain"]
       ["text" "plain"] ["text" "*"]     ["text" "plain"]
       ["*" "*"]        ["*" "*"]        ["*" "*"]
       ["text" "plain"] ["image" "*"]    nil))

(deftest test-best-allowed-content-type
  (let [browser-accepts "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"]
    (is (= ["text" "html"]       (best-allowed-content-type browser-accepts #{["text" "html"] ["application" "json"]})))
    (is (= ["application" "xml"] (best-allowed-content-type browser-accepts #{["application" "json"] ["application" "xml"]})))
    (is (= ["application" "edn"] (best-allowed-content-type browser-accepts #{["application" "edn"]})))))

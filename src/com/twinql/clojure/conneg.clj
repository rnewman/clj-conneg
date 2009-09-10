;;;
;;; TODO: sort by level for text/html. Maybe also sort by charset.
;;; Finally, compare by precedence rules:
;;;   1) text/html;level=1
;;;   2) text/html
;;;   3) text/*
;;;   4) */*
;;;   

(ns com.twinql.clojure.conneg
  (:refer-clojure)
  (:require [clojure.contrib.str-utils :as str-utils]))

(def accept-fragment-re
  #"^(\*|[^()<>@,;:\"/\[\]?={}         ]+)/(\*|[^()<>@,;:\"/\[\]?={}         ]+)$")

(def accept-fragment-param-re
  #"^([^()<>@,;:\"/\[\]?={} 	]+)=([^()<>@,;:\"/\[\]?={} 	]+|\"[^\"]*\")$")

(defn- clamp [minimum maximum val]
  (min maximum
       (max minimum val)))

(defn- parse-q [#^String str]
  (Double/parseDouble str))

(defn- assoc-param [coll n v]
  (try
    (assoc coll
           (keyword n) 
           (if (= "q" n)
             (clamp 0 1 (parse-q v))
             v))
    (catch Throwable e
      coll)))
    
(defn params->map [params]
  (loop
    [p (first params)
     ps (rest params)
     acc {}]
    (let [x (when p
              (rest (re-matches accept-fragment-param-re p)))
          accumulated (if (= 2 (count x))
                        (assoc-param acc (first x) (second x))
                        acc)]
      (if (empty? ps)
        accumulated
        (recur
          (first ps)
          (rest ps)
          accumulated)))))
 
(defn accept-fragment
  "Take something like
    \"text/html\"
  or
    \"image/*; q=0.8\"
  and return a map like
    {:type [\"image\" \"*\"]
      :q 0.8}
    
  If the fragment is invalid, nil is returned.
  
  Eventually, a `weights` map will be input, used to accord a server-side
  weight to a format."

  ([f]
   (let [parts (str-utils/re-split #"\s*;\s*" f)]
     (when (not (empty? parts))
       ;; First part will be a type.
       (let [type-str (first parts)
             type-pair (rest (re-matches accept-fragment-re type-str))]
         (when type-pair
           (assoc
             (params->map (rest parts))
             :type type-pair)))))))

(defn sort-by-q [coll]
  (reverse     ; Highest first.
    (sort-by #(get %1 :q 1)
             coll)))

(defn sorted-accept [h]
  (sort-by-q
    (map accept-fragment
         (str-utils/re-split #"\s*,\s*" h))))

;; TODO
(defn acceptable-type [type-pair acceptable]
  true)

(defn allowed-types-filter [allowed-types]
  (fn [accept]
    (some (partial acceptable-type accept)
          allowed-types)))

(defn best-allowed-content-type
  "Return the first type in the Accept header that is acceptable."
  ([accepts-header]
   (best-allowed-content-type accepts-header true))
  ([accepts-header
    allowed-types]    ; Set of strings or pairs. true/nil/:all for any.
   (:type
     (first
       (let [sorted (sorted-accept accepts-header)]
         (cond
           (contains? #{:all nil true} allowed-types)
           sorted

           (fn? allowed-types)
           (filter allowed-types sorted)

           true
           (filter (allowed-types-filter allowed-types)
                   sorted)))))))

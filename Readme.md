# clj-conneg

[![Build Status](https://travis-ci.org/mtnygard/clj-conneg.png)](https://travis-ci.org/mtnygard/clj-conneg)

A basic content negotiation library for Clojure.

# Usage

The primary interface function is

    com.twinql.clojure.conneg/best-allowed-content-type

which attempts to return the single best concrete type (e.g., if they specify
image/jpeg and you allow image/\*, it'll return image/jpeg, *and vice versa*).

You might also be interested in `sorted-accept` (at a lower level).

Usage:

    (require ['com.twinql.clojure.conneg :as 'conneg])

    (conneg/best-allowed-content-type
      "image/*; q=0.5, text/html, text/plain; q=0.8"
      #{"text/*"})
    =>
    ("text" "html")

    (conneg/best-allowed-content-type
      "image/*; q=0.5, text/html, text/plain; q=0.8")
    =>
    ("text" "html")

    (conneg/best-allowed-content-type
      "image/*; q=0.9, text/html; q=0.1, text/plain; q=0.8"
      #{["image" "jpeg"]})
    =>
    ("image" "jpeg")

    ;; Get your accept header from your web framework,
    ;; and filter by which types you can produce:
    (let [accept-header (:accept headers)]
      (conneg/best-allowed-content-type
        accept-header
        #{"text/html" "text/*"}))

    =>
    ["text" "plain"]

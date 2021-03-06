(ns getz)

(println "014 - get")

;; Dedicated to associative data structure,

(get {:a "a" :b "b"} :a)
(get (transient {:a "a" :b "b"}) :a)
(get (sorted-map :a "a" :b "b") :a)
(get {:a "a" :b "b"} :c ":c not found")
(get nil :k "nope")
(get nil nil "nope")

;; but access many other things.
;; what about sets?

(get #{"one" "two" "three"} "two")
(get (sorted-set "one" "two" "three") "two")

;; vectors?

(get ["zero" "one" "two"] 1)
(get (transient ["zero" "one" "two"]) 1)

;; Wow, what else?

(get "hello" 0)
(get (int-array [1 2 3]) 0)
(defrecord Address [street city code])
(get (Address. "High St" "Honolulu" "96805") :city)
(import 'java.util.HashMap)
(get (HashMap. {:a "a" :b "b"}) :b)

;; any surprise?

(get '("one" "two" "three") "one")
(get (transient #{"one" "two" "three"}) "one")
(get (sorted-map 'a "a" 'b "b") :a "not found")
(get [1 2 3 4] 4294967296)

;; One typical use

(defn select-matching [m k]
  (let [regex (re-pattern (str ".*" k ".*"))]
    (->> (keys m)
         (filter #(re-find regex (.toLowerCase %)))
         (reduce #(assoc %1 (keyword %2) (get m %2)) {}))))

(defn search [k]
  (merge (select-matching (System/getProperties) k)
         (select-matching (System/getenv) k)))

(search "dir")

;; Alternatives

(find {:a "a" :b "b"} :b)
({:a "a" :b "b"} :b)
(:b {:a "a" :b "b"})
(.get {:a "a" :b "b"} :b)

;; perf

## 008 defmacro

Screencast link: https://youtu.be/msxG2rHcUaM

Also see chapter 4.1 of the [Clojure Standard Library](https://www.manning.com/books/clojure-standard-library) book.

## Errata

* At around 6:15 I confused the meaning of invoking a Symbol as a function. The symbol is looked up in the collection passed as the first argument (an ILookup to be precise). If the first argument is not a collection (like the case here) the default is returned so:

        ('a {'a 1 'b 2})
        ;; 1

There is no lookup in the interned symbol table (which doesn't exist, I was confusing it with Keywords).

### Summary

* Growing a little debug helper, how to show the initial form?
* Problems showing nested forms
* Problems with evaluation of symbols and accidental capturing of locals
* A better approach based on syntax-quote
* Final defmacro version
* How defmacro works
* Using defn facilities
* Performance considerations

### At the REPL

```clojure

;; === a simple debugging tool ===

(/ (* 3 (+ 1 1)) 10.)
;; 0.6
;; But I'd like to see something like:
;; (+ 1 1) is 2
;; 0.6

(defn ? [form]
  (let [res (form)]
    (println form "is" res)
    res))

(/ (* 3 (? #(+ 1 1))) 10.)

(defn ? [form]
  (let [res (apply (first form) (rest form))]
    (println form "is" res)
    res))

(/ (* 3 (? '(+ 1 1))) 10.)

(type (first '(+ 1 1)))

('+ 1 1)

(defn ? [form]
	(let [res (apply (ns-resolve *ns* (first form)) (rest form))]
		(println form "is" res)
		res))

(/ (* 3 (? '(+ 1 1))) 10.)

(/ (? '(* 3 (? '(+ 1 1)))) 10.)

;; === A better approach ===

(defn ? [_ _ form]        ;; 2 additional params
  (let [res (eval form)]  ;; need to eval explicitly
    (println form "is" res)
    res))
(. (var ?) (setMacro))

(* 3 (? (+ 1 1)))
(/ (? (* 3 (? (+ 1 1)))) 10.)

(* 3 (let [res 5] (? (+ 1 res)))) ;; bang!

(* 3
   (let [res 5]
     (let [res (eval '(+ 1 res))]
       (println form "is" res)
       res)))

(eval '(+ 1 res)) ;; !!

;; === The correct approach ===

(defn ? [_ _ form]
  `(let [res# ~form]
     (println '~form '~'is res#)
     res#))
(. (var ?) (setMacro))

(* 3 (let [res 5] (? (+ 1 res)))

(defmacro ? [form]
  `(let [res# ~form]
     (println '~form '~'is res#)
     res#))

(* 3 (let [res 5] (? (+ 1 res)))) ;; finally!

(defmacro ? [form]
  `(let [res# ~form]
     (println '~&form '~'is res#)
     res#))

(* 3 (let [res 5] (? (+ 1 res)))) ;; finally!

;; === how does it work ===

(macroexpand '(defmacro simple []))

;; === contract ===

(defmacro ^:dbg ?
  "Prints the result of the intermediate evaluations."
  ([form]
   {:pre [(some? form)]}
   `(? ~form #'prn))
  ([form f]
   `(let [res# ~form]
      (~f '~form '~'is res#)
      res#)))

(* 3 (? nil)) ;; assertion error
(* 3 (? (+ 1 1) clojure.pprint/write)) ;; different printing

;; === perf ===

```

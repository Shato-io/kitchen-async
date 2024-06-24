(ns kitchen-async.promise
  (:refer-clojure :exclude [resolve])
  (:require-macros [kitchen-async.promise :as p])
  (:require goog.Promise
            [kitchen-async.protocols.promisable :as promisable]))

(def ^:private %promise-impl
  (let [init (if (exists? js/Promise)
               js/Promise
               goog.Promise)]
    (atom init)))

(defn promise-impl []
  @%promise-impl)

(defn set-promise-impl! [impl]
  (reset! %promise-impl impl))

(defn promise? [o]
  ;; https://stackoverflow.com/a/27746324/1956471
  (and (some? o)
       (fn? (.-then o))))

(defn resolve [x]
  (let [p (promise-impl)]
    (new p (fn [resolve] (resolve x)))))

(defn reject [x]
  (let [p (promise-impl)]
    (new p (fn [_ reject] (reject x)))))

(declare ->promise)

(defn then
  ([p f]
   (if (promise? p)
     (.then p (fn [x] (f x)))
     (f p)))
  ([p f g]
   (if (promise? p)
     (.then p (fn [x] (f x)) (fn [x] (g x)))
     (f p))))

(defn catch* [p f]
  (then p identity f))

(defn finally* [p f]
  (then p f f))

(defn all [ps]
  (if (some promise? ps)
    (goog.Promise.all (into-array (map ->promise ps)))
    ps))

(defn race [ps]
  (if-let [first-value-without-promise (first (filter (complement promise?) ps))]
    first-value-without-promise
    (goog.Promise.race (into-array (map ->promise ps)))))

(defn timeout
  ([ms] (timeout ms nil))
  ([ms v]
   (p/promise [resolve]
              (js/setTimeout #(resolve v) ms))))

(extend-protocol promisable/Promisable
  goog.Promise
  (->promise* [p] p)

  default
  (->promise* [x]
    (resolve x)))

(when (exists? js/Promise)
  (extend-type js/Promise
    promisable/Promisable
    (->promise* [p] p)))

(defn ->promise [x]
  (promisable/->promise* x))

(defn promisify
  "Given a fn that takes a callback fn as its last arg, and returns
  a modified version of that fn that returns a promise instead of
  calling the callback"
  [f]
  (fn [& args]
    (p/promise [resolve reject]
               (letfn [(callback [err val]
                         (if err
                           (reject err)
                           (resolve val)))]
                 (apply f (concat args [callback]))))))

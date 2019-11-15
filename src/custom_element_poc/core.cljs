(ns custom-element-poc.core
  (:require
    [vdom.core :as vdom]))

(enable-console-print!)

(defn attach-shadow [elem init]
  (.attachShadow elem (clj->js init)))

(defn defcomponent [component-fn]
  (let [component (fn component []
                    (let [custom-elem
                          (js/Reflect.construct js/HTMLElement #js [] component)
                          shadow
                          (attach-shadow custom-elem #js {:mode "open"})
                          render
                          (vdom/renderer shadow)]

                      (render (component-fn))))]

    ; extend the HTMLElement class
    (set! (.-prototype component) (js/Object.create (.-prototype js/HTMLElement)))

    component))

(def define-custom-element!
  (memoize
    (fn [tag component]
      (js/window.customElements.define tag component)
      component)))



(defn my-component []
  [:div {}
   [:h2 {} "My component"]
   [:p {} "Lorem ipsum"]])


(define-custom-element! "my-special" (defcomponent my-component))


(defn on-js-reload []
  ;(js/console.log (.-area (Rectangle. 3 5)) (MyRect. 3 5))
)

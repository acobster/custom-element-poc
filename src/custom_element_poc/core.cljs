(ns custom-element-poc.core
  (:require
    [vdom.core :as vdom]))

(enable-console-print!)

(println "This text is printed from src/custom-element-poc/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))


;(defn defcomponent [name ctor]
;	(let [;; defines the constructor function, which is the "class" object used by the customElements api
;				component (fn component [] (let [e
;																				 ;; this is the equivalent of the call to "super"
;																				 (js/Reflect.construct js/HTMLElement #js [] component)
;                                         p
;                                         (js/document.createElement "h1")]
;                                     (if (fn? ctor
;                                              (ctor)))
;																		 e))]
;		(set! (.-prototype component)
;					;; establishes prototype hierarchy
;					(js/Object.create (.-prototype js/HTMLElement)
;														#js {:connectedCallback
;																 #js {:configurable true
;																			:value        (fn []
;																											(this-as this
;																															 ;; attaches the reagent process to the shadow dom
;																															 ;(re/render [view-component] (.-shadow this))
;																															 (js/console.log (str name ": Connected! ") this)))}}))
;
;		;;finally, defines the component with these values
;		(js/window.customElements.define name component)
;
;		component))




;(defcomponent "my-element" (fn []
;                             (js/console.log "ctor")))

;(defn Shape []
;  (js/console.log "Shape constructor"))
;
;(defn Rectangle [x y]
;  ;(let [rect (js/Reflect.construct Shape #js [])]
;  (let [rect (js/Object.create (.-prototype Shape)
;                               #js {:area #js {:configurable true
;                                               :get (fn [] (* x y))}})]
;    (js/console.log "Rectangle constructor")
;    (this-as this
;             (set! (.-x this) x)
;             (set! (.-y this) y))
;
;    (set! (.-prototype rect)
;          (js/Object.create (.-prototype Shape)))
;
;    rect))
;
;(defn defclass [{:keys [:constructor :extends :properties]}]
;    (let [constructor (or constructor (fn []))
;          prototype (if extends (.-prototype extends) (.-prototype js/Object))
;          create-args (clj->js [prototype properties])
;          Class (fn [& args]
;                  (let [obj (js/Object.create prototype (or (clj->js properties) #js {}))]
;                    (if constructor (.apply constructor (clj->js args)))
;                    obj))]
;
;      (if extends
;        (set! (.-prototype.-constructor Class) Class))
;
;      Class))
;
;
;(def MyRect (defclass {:extends Shape
;                       :constructor (fn [x y]
;                                      (this-as this
;                                               (set! (.-x this) x)
;                                               (set! (.-y this) y)))
;                       :properties {:area {:configurable false
;                                           :get (fn [] (this-as
;                                                         this
;                                                         (* (.-x this) (.-y this))))}}}))

(comment
(defclass MyComponent :extends)
)




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

;                        #js {:foo #js {:configurable true
;                                       :writable true
;                                       :value "hi"}
;                             :area #js {:configurable false
;                                        :writable false
;                                        :value (fn []
;                                                 (this-as this
;                                                          (* (.-x this) (.-y this))))}}))
;

(define-custom-element! "my-special" (defcomponent my-component))


(defn on-js-reload []
  ;(js/console.log (.-area (Rectangle. 3 5)) (MyRect. 3 5))
  (js/console.log (render (my-component)))
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

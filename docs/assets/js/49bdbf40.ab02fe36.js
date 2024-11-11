"use strict";(self.webpackChunkdoodle_docs=self.webpackChunkdoodle_docs||[]).push([[8552],{8872:(e,n,o)=>{o.d(n,{o:()=>d});var t=o(1504),i=o(8556),a=o.n(i),l=o(312),p=o(1130),r=(o(5544),o(7624));class d extends t.Component{constructor(e){super(e),e.args&&(this.args=JSON.parse(e.args)),this.height=e.height,this.functionName="reactComponent",this.ref=t.createRef()}componentDidMount(){let e=this.ref.current.children[1];this.ref.current.removeChild(this.ref.current.children[1]),this.app=a()[this.functionName](this.ref.current.children[0],e)}componentWillUnmount(){this.app}render(){return(0,r.jsxs)("div",{className:l.c.doodle,ref:this.ref,children:[(0,r.jsx)("div",{style:{position:"relative",height:this.height+"px"}}),(0,r.jsx)(p.cp,{})]})}}},2224:(e,n,o)=>{o.r(n),o.d(n,{assets:()=>x,contentTitle:()=>f,default:()=>j,frontMatter:()=>w,metadata:()=>b,toc:()=>v});var t=o(7624),i=o(4552),a=o(7793),l=o(6236),p=o(5272),r=o(7492),d=o(8872),s=o(3220),c=o(2456),h=o(6388);const m="package io.nacular.doodle.docs.apps.nesting\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.controls.ColorPicker\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.docs.utils.doodleColor\nimport io.nacular.doodle.drawing.opacity\nimport io.nacular.doodle.layout.Insets\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.layout.constraints.fill\n\n//sampleStart\nclass InnerApp(display: Display): Application {\n    init {\n        // Shows a color picker\n        display += ColorPicker(doodleColor opacity 0.75f).apply {\n            changed += { _,_,new -> println(new) }\n        }\n\n        // The picker grows with the display, but is inset a little\n        display.layout = constrain(display.first(), fill(insets = Insets(2.0)))\n    }\n\n    override fun shutdown() {}\n}\n//sampleEnd",u="package io.nacular.doodle.docs.apps.nesting\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.application.ApplicationViewFactory\nimport io.nacular.doodle.application.Modules.Companion.PointerModule\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.core.center\nimport io.nacular.doodle.core.height\nimport io.nacular.doodle.core.width\nimport io.nacular.doodle.drawing.Color.Companion.White\nimport io.nacular.doodle.drawing.paint\nimport io.nacular.doodle.geometry.Rectangle\nimport io.nacular.doodle.geometry.centered\nimport io.nacular.doodle.utils.Resizer\nimport org.kodein.di.instance\nimport kotlin.math.min\n\n//sampleStart\nclass OuterApp(display: Display, appView: ApplicationViewFactory): Application {\n    init {\n        // NOTE: Any module needed by InnerApp MUST be provided to the factory.\n        // PointerModule used here to enable interaction with the color picker.\n        display += appView(modules = listOf(PointerModule)) {\n            InnerApp(display = instance()) // Init inner app\n        }.apply {\n            bounds = Rectangle(\n                min(400.0, display.width  - 20),\n                min(300.0, display.height - 20)\n            ).centered(at = display.center)\n\n            Resizer(this).apply { movable = false }\n        }\n\n        display.fill(White.paint)\n    }\n\n    override fun shutdown() {}\n}\n//sampleEnd",y="package outerapp\n\nimport io.nacular.doodle.application.ApplicationViewFactory.Companion.AppViewModule\nimport io.nacular.doodle.application.Modules.Companion.PointerModule\nimport io.nacular.doodle.application.application\nimport io.nacular.doodle.docs.apps.nesting.OuterApp\nimport org.kodein.di.instance\n\nfun main() {\n//sampleStart\n    // NOTE: Modules used by the outer app are not available to the inner one.\n    // The PointerModule is used here to allow resizing of the View that holds the inner app.\n    application(modules = listOf(AppViewModule, PointerModule)) {\n        OuterApp(display = instance(), appView = instance())\n    }\n//sampleEnd\n}\n",g="package htmlelementapp\n\nimport UsefulApp\nimport io.nacular.doodle.application.application\nimport org.w3c.dom.HTMLElement\n\n//sampleStart\nfun main(element: HTMLElement) {\n    // launch app within element\n    application(element) {\n        UsefulApp()\n    }\n}\n//sampleEnd",w={hide_title:!0,title:"Web"},f=void 0,b={id:"platform_specific/web",title:"Web",description:"Host apps in HTML elements",source:"@site/docs/platform_specific/web.mdx",sourceDirName:"platform_specific",slug:"/platform_specific/web",permalink:"/doodle/docs/platform_specific/web",draft:!1,unlisted:!1,tags:[],version:"current",frontMatter:{hide_title:!0,title:"Web"},sidebar:"tutorialSidebar",previous:{title:"Desktop",permalink:"/doodle/docs/platform_specific/desktop"}},x={},v=[{value:"Host apps in HTML elements",id:"host-apps-in-html-elements",level:2},{value:"Nested apps",id:"nested-apps",level:2},{value:"Host arbitrary HTML elements",id:"host-arbitrary-html-elements",level:2}];function M(e){const n={a:"a",admonition:"admonition",code:"code",h2:"h2",p:"p",strong:"strong",...(0,i.M)(),...e.components};return s.m||A("api",!1),s.m.AppViewModule||A("api.AppViewModule",!0),s.m.ApplicationViewFactory||A("api.ApplicationViewFactory",!0),s.m.View||A("api.View",!0),(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(n.h2,{id:"host-apps-in-html-elements",children:"Host apps in HTML elements"}),"\n",(0,t.jsx)(n.p,{children:"You can also provide an HTML element when launching a top-level Web app. This allows you to host Doodle apps in non-Doodle contexts. The apps in this documentation are top-level within specific elements."}),"\n",(0,t.jsxs)(n.p,{children:["Closing the page cleans up any apps within it. Removing the element hosting an app or explicitly calling ",(0,t.jsx)(n.code,{children:"shutdown"})," has the same effect."]}),"\n",(0,t.jsx)(r.A,{children:g}),"\n",(0,t.jsx)(n.h2,{id:"nested-apps",children:"Nested apps"}),"\n",(0,t.jsxs)(n.p,{children:["Doodle Web apps can be run within other Doodle Web apps. This is done by placing the nested app in a ",(0,t.jsx)(n.a,{href:"/doodle/docs/views",children:(0,t.jsx)(n.strong,{children:"View"})})," that the host app manages. An app launched this way has the same functionality as a top-level one. Its lifecycle however, is tied to the host View."]}),"\n",(0,t.jsxs)(n.p,{children:["You simply use an ",(0,t.jsx)(s.m.ApplicationViewFactory,{})," (available via the ",(0,t.jsx)(s.m.AppViewModule,{}),") to create nested apps."]}),"\n",(0,t.jsx)(p.u,{functionName:"nestedApp",height:"400"}),"\n",(0,t.jsxs)(a.c,{children:[(0,t.jsx)(l.c,{value:"Inner App",children:(0,t.jsx)(r.A,{children:m})}),(0,t.jsx)(l.c,{value:"Outer App",children:(0,t.jsx)(r.A,{children:u})}),(0,t.jsx)(l.c,{value:"Outer App Launcher",children:(0,t.jsx)(r.A,{children:y})})]}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsxs)(n.p,{children:["Adding a nested app's View to the ",(0,t.jsx)(n.a,{href:"/doodle/docs/display",children:(0,t.jsx)(n.strong,{children:"Display"})})," triggers the app's initialization. Shutdown the app by removing the host View from the Display."]})}),"\n",(0,t.jsx)(n.h2,{id:"host-arbitrary-html-elements",children:"Host arbitrary HTML elements"}),"\n",(0,t.jsxs)(n.p,{children:["You can embed any HTML element into your app as a ",(0,t.jsx)(s.m.View,{}),". This means Doodle apps can host React and other web components and interop with a much larger part of the Web ecosystem out of the box!"]}),"\n",(0,t.jsx)(d.o,{height:"400"}),"\n",(0,t.jsxs)(a.c,{children:[(0,t.jsx)(l.c,{value:"App",children:(0,t.jsx)(r.A,{children:c.c})}),(0,t.jsx)(l.c,{value:"Example Launcher",children:(0,t.jsx)(r.A,{children:h.c})})]}),"\n",(0,t.jsx)(n.admonition,{type:"info",children:(0,t.jsxs)(n.p,{children:["This app embeds a ",(0,t.jsx)(n.a,{href:"https://projects.wojtekmaj.pl/react-calendar/",children:"react-calendar"}),"."]})})]})}function j(e={}){const{wrapper:n}={...(0,i.M)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(M,{...e})}):M(e)}function A(e,n){throw new Error("Expected "+(n?"component":"object")+" `"+e+"` to be defined: you likely forgot to import, pass, or provide it.")}},2456:(e,n,o)=>{o.d(n,{c:()=>t});const t='package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.HtmlElementViewFactory\nimport io.nacular.doodle.animation.Animator\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.controls.text.Label\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.core.then\nimport io.nacular.doodle.docs.utils.DateRangeSelectionModel\nimport io.nacular.doodle.docs.utils.HorizontalCalendar\nimport io.nacular.doodle.docs.utils.ShadowCard\nimport io.nacular.doodle.drawing.Font\nimport io.nacular.doodle.geometry.PathMetrics\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.theme.Theme\nimport io.nacular.doodle.theme.ThemeManager\nimport kotlinx.datetime.DatePeriod\nimport kotlinx.datetime.LocalDate\nimport kotlinx.datetime.plus\nimport org.w3c.dom.HTMLElement\n\nclass ReactCalendarApp(\n    display        : Display,\n    font           : Font,\n    today          : LocalDate,\n    animate        : Animator,\n    pathMetrics    : PathMetrics,\n    themeManager   : ThemeManager,\n    theme          : Theme,\n    htmlElementView: HtmlElementViewFactory,\n    reactCalendar  : HTMLElement,\n    appHeight      : (Double) -> Unit\n): Application {\n\n    private val doodleCalendar = HorizontalCalendar(\n        today,\n        animate,\n        pathMetrics,\n        startDate = today,\n        endDate   = today + DatePeriod(years = 10),\n        DateRangeSelectionModel()\n    ).apply {\n        this.font = font\n    }\n\n    init {\n        themeManager.selected = theme\n\n//sampleStart\n        display += Label("Doodle").apply { this.font = font }\n        display += ShadowCard(doodleCalendar)\n        display += Label("React").apply { this.font = font }\n        display += htmlElementView(element = reactCalendar)\n//sampleEnd\n\n        val spacing = 20\n\n        display.layout = constrain(\n            display.children[0],\n            display.children[1],\n            display.children[2],\n            display.children[3]\n        ) { doodleLabel, doodle, reactLabel, react ->\n\n            doodle.top          eq doodleLabel.bottom + spacing\n            doodle.height       eq 280\n            react.height        eq doodle.height\n\n            doodleLabel.top     eq spacing\n            doodleLabel.centerX eq doodle.centerX\n            doodleLabel.width.preserve\n            doodleLabel.height.preserve\n\n            reactLabel.centerX  eq react.centerX\n            reactLabel.width.preserve\n            reactLabel.height.preserve\n\n            when {\n                parent.width.readOnly > 800 -> {\n                    doodle.width    eq (parent.width - 3 * spacing) / 2\n                    doodle.right    eq parent.centerX - spacing / 2\n                    react.top       eq doodle.top\n                    react.width     eq doodle.width\n                    react.left      eq doodle.right + spacing\n                    reactLabel.top  eq doodleLabel.top\n                }\n                else -> {\n                    doodle.left     eq spacing\n                    doodle.right    eq parent.right - spacing\n                    react.top       eq reactLabel.bottom + spacing\n                    react.left      eq spacing\n                    react.right     eq parent.right - spacing\n                    reactLabel.top  eq doodle.bottom + spacing\n                }\n            }\n        }.then { container ->\n            // signal to outer docs about height of the app\n            appHeight(container.children.maxOf { it.bounds.bottom } + spacing)\n        }\n    }\n\n    override fun shutdown() {\n        // no-op\n    }\n}'},6388:(e,n,o)=>{o.d(n,{c:()=>t});const t="package elementview\n\nimport io.nacular.doodle.HtmlElementViewFactory\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.application.HtmlElementViewModule\nimport io.nacular.doodle.application.Modules\nimport io.nacular.doodle.application.application\nimport io.nacular.doodle.core.Display\nimport org.kodein.di.instance\nimport org.w3c.dom.HTMLElement\n\nprivate class MyApp(\n    display    : Display,\n    viewFactory: HtmlElementViewFactory,\n    element    : HTMLElement\n): Application {\n    init {\n        display += viewFactory(element)\n    }\n\n    override fun shutdown() {}\n}\n\nfun main(element: HTMLElement) {\n//sampleStart\n    application(modules = listOf(Modules.HtmlElementViewModule)) {\n         MyApp(display = instance(), viewFactory = instance(), element = element)\n    }\n//sampleEnd\n}\n"}}]);
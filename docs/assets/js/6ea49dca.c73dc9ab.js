"use strict";(self.webpackChunkdoodle_docs=self.webpackChunkdoodle_docs||[]).push([[2088],{1716:(e,n,o)=>{o.r(n),o.d(n,{assets:()=>P,contentTitle:()=>k,default:()=>C,frontMatter:()=>A,metadata:()=>M,toc:()=>D});var i=o(7624),t=o(2172),a=o(1268),r=o(5388),l=o(5720),s=o(7996),d=o(1504),c=o(5972),p=o.n(c),m=o(312),h=o(1130);o(5544);class u extends d.Component{constructor(e){super(e),e.args&&(this.args=JSON.parse(e.args)),this.height=e.height,this.functionName="reactComponent",this.ref=d.createRef()}componentDidMount(){let e=this.ref.current.children[1];this.ref.current.removeChild(this.ref.current.children[1]),this.app=p()[this.functionName](this.ref.current.children[0],e)}componentWillUnmount(){this.app&&p().shutDownApp(this.app)}render(){return(0,i.jsxs)("div",{className:m.c.doodle,ref:this.ref,children:[(0,i.jsx)("div",{style:{position:"relative",height:this.height+"px"}}),(0,i.jsx)(h.cp,{})]})}}var w=o(3148);const g='package display\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.core.WindowGroup\nimport io.nacular.doodle.core.view\nimport io.nacular.doodle.geometry.Size\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.layout.constraints.fill\n\n//sampleStart\nclass MyCoolApp(windows: WindowGroup): Application {\n    init {\n        // main window\'s display, same as if Display were injected\n        windows.main.apply {\n            title = "Main Window"\n\n            // manipulate main window\'s display\n            display += view {}\n        }\n\n        // create a new window\n        windows {\n            title                = "A New Window!"\n            size                 = Size(500)\n            enabled              = false\n            resizable            = false\n            triesToAlwaysBeOnTop = true\n\n            // manipulate the new window\'s display\n            display += view {}\n            display.layout = constrain(display.first(), fill)\n\n            closed += {\n                // handle window close\n            }\n        }\n    }\n\n    override fun shutdown() {}\n}\n//sampleEnd',y='package display\n\nimport io.nacular.doodle.controls.popupmenu.MenuBehavior.ItemInfo\nimport io.nacular.doodle.core.Icon\nimport io.nacular.doodle.core.Window\n\nfun example(window: Window, icon1: Icon<ItemInfo>, icon2: Icon<ItemInfo>) {\n//sampleStart\n    window.menuBar {\n        menu("Menu 1") {\n            action("Do action 2", icon1) { /*..*/ }\n            menu("Sub menu") {\n                action("Do action sub", icon = icon2) { /*..*/ }\n                separator()\n                prompt("Some Prompt sub") { /*..*/ }\n            }\n            separator()\n            prompt("Some Prompt") { /*..*/ }\n        }\n\n        menu("Menu 2") {\n            // ...\n        }\n    }\n//sampleEnd\n}',f='package display\n\nimport io.nacular.doodle.controls.popupmenu.MenuBehavior.ItemInfo\nimport io.nacular.doodle.core.Icon\nimport io.nacular.doodle.core.Window\nimport io.nacular.doodle.geometry.Point\n\nfun contextMenu(window: Window, icon1: Icon<ItemInfo>, icon2: Icon<ItemInfo>) {\n//sampleStart\n    window.popupMenu(at = Point()) {\n        action("Do action 2", icon1) { /*..*/ }\n        menu("Sub menu") {\n            action("Do action sub", icon = icon2) { /*..*/ }\n            separator()\n            prompt("Some Prompt sub") { /*..*/ }\n        }\n        separator()\n        prompt("Some Prompt") { /*..*/ }\n    }\n//sampleEnd\n}',v='package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.HtmlElementViewFactory\nimport io.nacular.doodle.animation.Animator\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.controls.text.Label\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.core.then\nimport io.nacular.doodle.docs.utils.DateRangeSelectionModel\nimport io.nacular.doodle.docs.utils.HorizontalCalendar\nimport io.nacular.doodle.docs.utils.ShadowCard\nimport io.nacular.doodle.drawing.Font\nimport io.nacular.doodle.geometry.PathMetrics\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.theme.Theme\nimport io.nacular.doodle.theme.ThemeManager\nimport kotlinx.datetime.DatePeriod\nimport kotlinx.datetime.LocalDate\nimport kotlinx.datetime.plus\nimport org.w3c.dom.HTMLElement\n\nclass ReactCalendarApp(\n    display        : Display,\n    font           : Font,\n    today          : LocalDate,\n    animate        : Animator,\n    pathMetrics    : PathMetrics,\n    themeManager   : ThemeManager,\n    theme          : Theme,\n    htmlElementView: HtmlElementViewFactory,\n    reactCalendar  : HTMLElement,\n    appHeight      : (Double) -> Unit\n): Application {\n\n    private val doodleCalendar = HorizontalCalendar(\n        today,\n        animate,\n        pathMetrics,\n        startDate = today,\n        endDate   = today + DatePeriod(years = 10),\n        DateRangeSelectionModel()\n    ).apply {\n        this.font = font\n    }\n\n    init {\n        themeManager.selected = theme\n\n//sampleStart\n        display += Label("Doodle").apply { this.font = font }\n        display += ShadowCard(doodleCalendar)\n        display += Label("React").apply { this.font = font }\n        display += htmlElementView(element = reactCalendar)\n//sampleEnd\n\n        val spacing = 20\n\n        display.layout = constrain(\n            display.children[0],\n            display.children[1],\n            display.children[2],\n            display.children[3]\n        ) { doodleLabel, doodle, reactLabel, react ->\n\n            doodle.top          eq doodleLabel.bottom + spacing\n            doodle.height       eq 280\n            react.height        eq doodle.height\n\n            doodleLabel.top     eq spacing\n            doodleLabel.centerX eq doodle.centerX\n            doodleLabel.width.preserve\n            doodleLabel.height.preserve\n\n            reactLabel.centerX  eq react.centerX\n            reactLabel.width.preserve\n            reactLabel.height.preserve\n\n            when {\n                parent.width.readOnly > 800 -> {\n                    doodle.width    eq (parent.width - 3 * spacing) / 2\n                    doodle.right    eq parent.centerX - spacing / 2\n                    react.top       eq doodle.top\n                    react.width     eq doodle.width\n                    react.left      eq doodle.right + spacing\n                    reactLabel.top  eq doodleLabel.top\n                }\n                else -> {\n                    doodle.left     eq spacing\n                    doodle.right    eq parent.right - spacing\n                    react.top       eq reactLabel.bottom + spacing\n                    react.left      eq spacing\n                    react.right     eq parent.right - spacing\n                    reactLabel.top  eq doodle.bottom + spacing\n                }\n            }\n        }.then { container ->\n            // signal to outer docs about height of the app\n            appHeight(container.children.maxOf { it.bounds.bottom } + spacing)\n        }\n    }\n\n    override fun shutdown() {\n        // no-op\n    }\n}',b="package elementview\n\nimport io.nacular.doodle.HtmlElementViewFactory\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.application.HtmlElementViewModule\nimport io.nacular.doodle.application.Modules\nimport io.nacular.doodle.application.application\nimport io.nacular.doodle.core.Display\nimport org.kodein.di.instance\nimport org.w3c.dom.HTMLElement\n\nprivate class MyApp(\n    display    : Display,\n    viewFactory: HtmlElementViewFactory,\n    element    : HTMLElement\n): Application {\n    init {\n        display += viewFactory(element)\n    }\n\n    override fun shutdown() {}\n}\n\nfun main(element: HTMLElement) {\n//sampleStart\n    application(modules = listOf(Modules.HtmlElementViewModule)) {\n         MyApp(display = instance(), viewFactory = instance(), element = element)\n    }\n//sampleEnd\n}\n",x='package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.controls.buttons.PushButton\nimport io.nacular.doodle.controls.form.Always\nimport io.nacular.doodle.controls.form.Form\nimport io.nacular.doodle.controls.form.labeled\nimport io.nacular.doodle.controls.form.textField\nimport io.nacular.doodle.controls.form.verticalLayout\nimport io.nacular.doodle.controls.text.TextField\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.drawing.Font\nimport io.nacular.doodle.event.KeyCode.Companion.Enter\nimport io.nacular.doodle.event.KeyListener\nimport io.nacular.doodle.geometry.Point\nimport io.nacular.doodle.geometry.Size\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.theme.Theme\nimport io.nacular.doodle.theme.ThemeManager\nimport io.nacular.doodle.utils.Resizer\n\nclass EnterKeyInterceptApp(\n    display     : Display,\n    font        : Font,\n    themeManager: ThemeManager,\n    theme       : Theme\n): Application {\n    private lateinit var name    : TextField\n    private lateinit var password: TextField\n\n    private val submit = PushButton("Submit").apply {\n        this.font    = font\n        this.size    = Size(100, 32)\n        this.enabled = false\n\n        this.fired += {\n            name.text     = ""\n            password.text = ""\n        }\n    }\n\n    private val form = Form { this (\n        + labeled("Name",     showRequired = Always()) { textField(Regex(".{3,}")) { name     = textField } },\n        + labeled("Password", showRequired = Always()) { textField(Regex(".{3,}")) { password = textField } },\n        onInvalid = { submit.enabled = false },\n    ) { _,_ ->\n        submit.enabled = true\n    } }.apply {\n        this.font      = font\n        this.size      = Size(300, 100)\n        this.layout    = verticalLayout(this, spacing = 12.0, itemHeight = 32.0)\n        this.focusable = false\n        Resizer(this).apply { movable = false }\n    }\n\n    init {\n        themeManager.selected = theme\n\n//sampleStart\n        form.keyFilter += KeyListener.pressed {\n            if (it.code == Enter && submit.enabled) {\n                it.consume()\n                submit.click()\n            }\n        }\n//sampleEnd\n\n        display += listOf(form, submit)\n\n        display.layout = constrain(form, submit) { form_, submit_ ->\n            val spacing = 10\n\n            form_.center    eq parent.center - Point(y = (spacing + submit_.height.readOnly) / 2)\n            submit_.top     eq form_.bottom + spacing\n            submit_.centerX eq form_.centerX\n        }\n    }\n\n    override fun shutdown() {}\n}';var j=o(6068);const S="package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.core.View\nimport io.nacular.doodle.core.container\nimport io.nacular.doodle.core.renderProperty\nimport io.nacular.doodle.docs.utils.BlueColor\nimport io.nacular.doodle.drawing.Canvas\nimport io.nacular.doodle.drawing.Color.Companion.Black\nimport io.nacular.doodle.drawing.Color.Companion.White\nimport io.nacular.doodle.drawing.SweepGradientPaint\nimport io.nacular.doodle.drawing.opacity\nimport io.nacular.doodle.drawing.paint\nimport io.nacular.doodle.event.PointerEvent\nimport io.nacular.doodle.event.PointerMotionListener\nimport io.nacular.doodle.geometry.Point\nimport io.nacular.doodle.geometry.Size\nimport io.nacular.doodle.geometry.ringSection\nimport io.nacular.doodle.layout.constraints.center\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.layout.constraints.fill\nimport io.nacular.doodle.utils.lerp\nimport io.nacular.measured.units.Angle.Companion.degrees\nimport io.nacular.measured.units.times\nimport kotlin.math.max\nimport kotlin.math.min\n\nclass SweepGradientProgressApp(display: Display): Application {\n\n    private class Progress: View() {\n        var progress by renderProperty(1f)\n\n        private val thickness  =  50.0\n        private val startAngle =  20 * degrees\n        private val endAngle   = 360 * degrees\n\n        init {\n            clipCanvasToBounds = false\n        }\n\n        override fun render(canvas: Canvas) {\n            val center      = Point(width / 2, height / 2)\n            val outerRadius = min(center.x, center.y)\n\n//sampleStart\n            canvas.outerShadow(vertical = 10.0, blurRadius = 10.0, color = Black opacity 0.5f) {\n                path(\n                    ringSection(\n                        center      = center,\n                        innerRadius = max(0.0, outerRadius - thickness),\n                        outerRadius = outerRadius,\n                        start       = startAngle,\n                        end         = lerp(startAngle, endAngle, progress),\n                        endCap      = { _,it ->\n                            arcTo(it, radius = thickness / 2, largeArch = true, sweep = true)\n                        }\n                    ),\n                    SweepGradientPaint(\n                        color1      = BlueColor opacity 0f,\n                        color2      = BlueColor opacity 1f,\n                        center      = center,\n                        rotation    = startAngle\n                    )\n                )\n            }\n//sampleEnd\n        }\n    }\n\n    init {\n        display += container {\n            val bar = Progress().apply { size = Size(200) }\n\n            +bar\n            layout = constrain(bar, center)\n\n            val updateProgress = { event: PointerEvent ->\n                bar.progress = (toLocal(event.location, event.target).x / width).toFloat()\n            }\n\n            pointerMotionChanged += PointerMotionListener.on(\n                moved   = updateProgress,\n                dragged = updateProgress\n            )\n        }\n\n        display.layout = constrain(display.first(), fill)\n\n        display.fill(White.paint)\n    }\n\n    override fun shutdown() {}\n}",A={hide_title:!0,title:"Whats new in Doodle"},k=void 0,M={id:"whatsnew",title:"Whats new in Doodle",description:"New in Doodle 0.10.1",source:"@site/docs/whatsnew.mdx",sourceDirName:".",slug:"/whatsnew",permalink:"/doodle/docs/whatsnew",draft:!1,unlisted:!1,tags:[],version:"current",frontMatter:{hide_title:!0,title:"Whats new in Doodle"},sidebar:"tutorialSidebar",previous:{title:"Installation",permalink:"/doodle/docs/installation"},next:{title:"Applications",permalink:"/doodle/docs/applications"}},P={},D=[{value:"New in Doodle 0.10.1",id:"new-in-doodle-0101",level:2},{value:"Sweep Gradient Paint",id:"sweep-gradient-paint",level:3},{value:"New in Doodle 0.10.0",id:"new-in-doodle-0100",level:2},{value:"Host arbitrary HTML elements (Browser)",id:"host-arbitrary-html-elements-browser",level:3},{value:"WASM JS (Browser)",id:"wasm-js-browser",level:3},{value:"Multi-window apps (Desktop)",id:"multi-window-apps-desktop",level:3},{value:"Native window menus (Desktop)",id:"native-window-menus-desktop",level:3},{value:"Native context menus",id:"native-context-menus",level:3},{value:"Key event filters and bubbling (All Platforms)",id:"key-event-filters-and-bubbling-all-platforms",level:3}];function T(e){const n={a:"a",admonition:"admonition",code:"code",h2:"h2",h3:"h3",li:"li",p:"p",ul:"ul",...(0,t.M)(),...e.components};return w||L("api",!1),w.BasicCircularProgressIndicatorBehavior||L("api.BasicCircularProgressIndicatorBehavior",!0),w.BasicCircularRangeSliderBehavior||L("api.BasicCircularRangeSliderBehavior",!0),w.SweepGradientPaint||L("api.SweepGradientPaint",!0),w.ViewKeyChanged||L("api.ViewKeyChanged",!0),w.ViewKeyFilter||L("api.ViewKeyFilter",!0),(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.h2,{id:"new-in-doodle-0101",children:"New in Doodle 0.10.1"}),"\n",(0,i.jsx)(n.p,{children:(0,i.jsx)(n.a,{href:"https://github.com/nacular/doodle/releases/tag/v0.10.1",children:"Released: May 3, 2024"})}),"\n",(0,i.jsxs)(n.p,{children:["This version is mostly focused on bug fixes, but it also includes a new ",(0,i.jsx)(n.code,{children:"Paint"})," type."]}),"\n",(0,i.jsx)(n.h3,{id:"sweep-gradient-paint",children:"Sweep Gradient Paint"}),"\n",(0,i.jsxs)(n.p,{children:["You can now render content using the new ",(0,i.jsx)(w.SweepGradientPaint,{}),". This paint creates a smooth gradient between colors around a center point. It is a great match for radial progress indicators."]}),"\n",(0,i.jsx)(s.u,{functionName:"sweepGradientPaint",height:"300"}),"\n",(0,i.jsx)(l.A,{children:j.c}),"\n",(0,i.jsx)(n.p,{children:"This new paint makes it easy to create gradient controls like this simple progress indicator."}),"\n",(0,i.jsxs)(a.c,{children:[(0,i.jsx)(r.c,{value:"Example",children:(0,i.jsx)(s.u,{functionName:"sweepGradientProgress",height:"300"})}),(0,i.jsx)(r.c,{value:"Code",children:(0,i.jsx)(l.A,{children:S})})]}),"\n",(0,i.jsx)(n.admonition,{type:"tip",children:(0,i.jsxs)(n.p,{children:["You can also use this paint with circular ",(0,i.jsx)(n.a,{href:"/doodle/docs/ui_components/overview#progressbar",children:"ProgressIndicator"})," and ",(0,i.jsx)(n.a,{href:"/doodle/docs/ui_components/overview#circularslider",children:"CircularRangeSlider"})," via ",(0,i.jsx)(w.BasicCircularProgressIndicatorBehavior,{})," and ",(0,i.jsx)(w.BasicCircularRangeSliderBehavior,{})," respectively."]})}),"\n",(0,i.jsx)(n.h2,{id:"new-in-doodle-0100",children:"New in Doodle 0.10.0"}),"\n",(0,i.jsx)(n.p,{children:(0,i.jsx)(n.a,{href:"https://github.com/nacular/doodle/releases/tag/v0.10.0",children:"Released: February 20, 2024"})}),"\n",(0,i.jsx)(n.p,{children:"The latest version of Doodle brings lots of important updates, especially in terms of better platform support for both Browser and Desktop. Some of the key highlights include:"}),"\n",(0,i.jsxs)(n.ul,{children:["\n",(0,i.jsx)(n.li,{children:"New ability to host embed arbitrary HTML elements as Views on Web"}),"\n",(0,i.jsx)(n.li,{children:"WASM JS support"}),"\n",(0,i.jsx)(n.li,{children:"Multiple windows in Desktop apps"}),"\n",(0,i.jsx)(n.li,{children:"OS menu bars in Desktop"}),"\n",(0,i.jsx)(n.li,{children:"More native context menus in Desktop apps"}),"\n"]}),"\n",(0,i.jsx)(n.h3,{id:"host-arbitrary-html-elements-browser",children:"Host arbitrary HTML elements (Browser)"}),"\n",(0,i.jsx)(n.p,{children:"You can now embed any HTML element into your app as a View. This means Doodle apps can now host React and other web components and interop with a much larger part of the Web ecosystem out of the box!"}),"\n",(0,i.jsx)(u,{height:"400"}),"\n",(0,i.jsxs)(a.c,{children:[(0,i.jsx)(r.c,{value:"App",children:(0,i.jsx)(l.A,{children:v})}),(0,i.jsx)(r.c,{value:"Example Launcher",children:(0,i.jsx)(l.A,{children:b})})]}),"\n",(0,i.jsx)(n.admonition,{type:"info",children:(0,i.jsxs)(n.p,{children:["This app embeds a ",(0,i.jsx)(n.a,{href:"https://projects.wojtekmaj.pl/react-calendar/",children:"react-calendar"}),"."]})}),"\n",(0,i.jsx)(n.h3,{id:"wasm-js-browser",children:"WASM JS (Browser)"}),"\n",(0,i.jsxs)(n.p,{children:["Doodle now supports the ",(0,i.jsx)(n.a,{href:"/docs/installation",children:"WasmJS"})," build target. This means apps can also target WebAssembly for the Browser. The APIs/features for this new target are identical as those for the ",(0,i.jsx)(n.code,{children:"js"})," target; which means code can be shared between apps targeting both. The only difference is that the ",(0,i.jsx)(n.code,{children:"application"})," launchers need to be called from separate source sets (i.e. ",(0,i.jsx)(n.code,{children:"jsMain"})," vs ",(0,i.jsx)(n.code,{children:"wasmJsMain"}),")."]}),"\n",(0,i.jsx)(n.h3,{id:"multi-window-apps-desktop",children:"Multi-window apps (Desktop)"}),"\n",(0,i.jsxs)(n.p,{children:["Apps for Desktop can now create/manage multiple windows using the new ",(0,i.jsx)(n.code,{children:"WindowGroup"})," interface. Simply inject it into your app to get started. The API provides access to an app's ",(0,i.jsx)(n.code,{children:"main"})," window as well as methods for creating new windows. Single window apps continue to work as they did before. That is, an app that injects the ",(0,i.jsx)(n.code,{children:"Display"})," will receive the ",(0,i.jsx)(n.code,{children:"main"})," window display and can manipulate it as before. But apps that want to manage their window(s) will need to inject this new type."]}),"\n",(0,i.jsx)(l.A,{children:g}),"\n",(0,i.jsx)(n.admonition,{type:"tip",children:(0,i.jsxs)(n.p,{children:["There's no need to inject ",(0,i.jsx)(n.code,{children:"Display"})," if you already inject ",(0,i.jsx)(n.code,{children:"WindowGroup"}),". That's because the injected ",(0,i.jsx)(n.code,{children:"Display"})," is equivalent to ",(0,i.jsx)(n.code,{children:"windowGroup.main.display"})]})}),"\n",(0,i.jsx)(n.h3,{id:"native-window-menus-desktop",children:"Native window menus (Desktop)"}),"\n",(0,i.jsx)(n.p,{children:"Apps can now set up native menus for their windows. This looks a lot like working with the existing menu APIs, but it results in changes to the OS window decoration. These menus are just as interactive as the in-app ones as well, meaning they trigger events when the user interacts with them."}),"\n",(0,i.jsx)(l.A,{children:y}),"\n",(0,i.jsx)(n.h3,{id:"native-context-menus",children:"Native context menus"}),"\n",(0,i.jsx)(n.p,{children:"Apps can now set up native context/popup menus for their windows. The API is very similar to native menus."}),"\n",(0,i.jsx)(l.A,{children:f}),"\n",(0,i.jsx)(n.h3,{id:"key-event-filters-and-bubbling-all-platforms",children:"Key event filters and bubbling (All Platforms)"}),"\n",(0,i.jsxs)(n.p,{children:['Key events now "sink" and "bubble" like pointer events. This means ancestor Views can intercept (and veto) them before they are delivered to their target (the focused View). They also bubble up to ancestors after being delivered to the target if they are not consumed. The notifications for the first phase happen via a new ',(0,i.jsx)(w.ViewKeyFilter,{})," property, while the bubbling phase is notified via the existing ",(0,i.jsx)(w.ViewKeyChanged,{})," property."]}),"\n",(0,i.jsxs)(n.p,{children:["This change makes it much easier to create Views like the following; which intercepts the ",(0,i.jsx)(n.code,{children:"ENTER"})," key to press the submit button."]}),"\n",(0,i.jsx)(s.u,{functionName:"enterKeyIntercept",height:"300"}),"\n",(0,i.jsx)(l.A,{children:x})]})}function C(e={}){const{wrapper:n}={...(0,t.M)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(T,{...e})}):T(e)}function L(e,n){throw new Error("Expected "+(n?"component":"object")+" `"+e+"` to be defined: you likely forgot to import, pass, or provide it.")}},6068:(e,n,o)=>{o.d(n,{c:()=>i});const i="package rendering\n\nimport io.nacular.doodle.core.view\nimport io.nacular.doodle.drawing.Color\nimport io.nacular.doodle.drawing.GradientPaint.Stop\nimport io.nacular.doodle.drawing.SweepGradientPaint\nimport io.nacular.doodle.geometry.Point\nimport io.nacular.measured.units.Angle\nimport io.nacular.measured.units.Measure\n\n/**\n * Example showing how to use [SweepGradientPaint]s.\n */\nfun sweepGradientPaint(color1: Color, color2: Color, center: Point, rotation: Measure<Angle>) {\n//sampleStart\n    view {\n        render = {\n            // Simple version with 2 colors\n            rect(bounds.atOrigin, SweepGradientPaint(\n                color1,\n                color2,\n                center,\n                rotation\n            ))\n        }\n    }\n\n    view {\n        render = {\n            // Also able to use a list of color stops\n            rect(\n                bounds.atOrigin, SweepGradientPaint(\n                    listOf(\n                        Stop(color1, 0f),\n                        Stop(color1, 1f / 3),\n                        // ...\n                    ),\n                    center,\n                    rotation\n                )\n            )\n        }\n    }\n//sampleEnd\n}"}}]);
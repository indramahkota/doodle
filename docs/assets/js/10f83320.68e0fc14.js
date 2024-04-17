"use strict";(self.webpackChunkdoodle_docs=self.webpackChunkdoodle_docs||[]).push([[2080],{9288:(n,e,o)=>{o.d(e,{cp:()=>l});var t=o(7624),i=o(2172),a=(o(1268),o(5388),o(5720));function r(n){const e={admonition:"admonition",p:"p",...(0,i.M)(),...n.components};return(0,t.jsxs)(e.admonition,{title:"Module Required",type:"info",children:[(0,t.jsxs)("p",{children:["You must include the ",n.link," in your application in order to use these features."]}),(0,t.jsx)(a.A,{children:n.module}),(0,t.jsx)(e.p,{children:"Doodle uses opt-in modules like this to improve bundle size."})]})}function l(n={}){const{wrapper:e}={...(0,i.M)(),...n.components};return e?(0,t.jsx)(e,{...n,children:(0,t.jsx)(r,{...n})}):r(n)}},4096:(n,e,o)=>{o.r(e),o.d(e,{assets:()=>v,contentTitle:()=>y,default:()=>S,frontMatter:()=>f,metadata:()=>w,toc:()=>k});var t=o(7624),i=o(2172),a=(o(1268),o(5388),o(5720)),r=o(7996),l=o(3148),s=o(9288);const d='package rendering\n\nimport io.nacular.doodle.core.view\nimport io.nacular.doodle.drawing.Color.Companion.Black\nimport io.nacular.doodle.drawing.text\nimport io.nacular.doodle.geometry.Point.Companion.Origin\n\nfun text() {\n    val textView = view {\n//sampleStart\n        render = {\n            text("hello", Origin, color = Black)\n        }\n//sampleEnd\n    }\n}',c='package rendering\n\nimport io.nacular.doodle.drawing.Font\nimport io.nacular.doodle.drawing.FontLoader\nimport kotlinx.coroutines.async\nimport kotlinx.coroutines.coroutineScope\n\nsuspend fun fontUrl(fonts: FontLoader) = coroutineScope {\n//sampleStart\n    async {\n        // Load this front from the file at "urlToFont"\n        val font: Font? = fonts("urlToFont") {\n            family = "Roboto"\n            size   = 14\n            weight = 400\n        }\n    }\n//sampleEnd\n}',p='package rendering\n\nimport io.nacular.doodle.drawing.Font\nimport io.nacular.doodle.drawing.FontLoader\nimport kotlinx.coroutines.CoroutineScope\nimport kotlinx.coroutines.Deferred\nimport kotlinx.coroutines.async\nimport kotlinx.coroutines.coroutineScope\n\nsuspend fun systemFont(scope: CoroutineScope, fonts: FontLoader) = coroutineScope {\n//sampleStart\n    // launch a new coroutine for async font lookup\n    val font: Deferred<Font?> = async {\n        fonts {\n            family = "Roboto"\n            size   = 14\n            weight = 400\n        }\n    }\n\n    //...\n\n    font.await()\n//sampleEnd\n}',u="package rendering\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.application.Modules.Companion.FontModule\nimport io.nacular.doodle.application.application\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.drawing.FontLoader\nimport org.kodein.di.instance\n\nclass FontLoaderApp(display: Display, fonts: FontLoader): Application {\n    override fun shutdown() {}\n}\n\nfun main() {\n//sampleStart\n    application(modules = listOf(FontModule)) {\n        FontLoaderApp(display = instance(), fonts = instance())\n    }\n//sampleEnd\n}",h='package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.core.view\nimport io.nacular.doodle.docs.utils.controlBackgroundColor\nimport io.nacular.doodle.drawing.Color.Companion.Red\nimport io.nacular.doodle.drawing.Color.Companion.White\nimport io.nacular.doodle.drawing.Color.Companion.Yellow\nimport io.nacular.doodle.drawing.FontLoader\nimport io.nacular.doodle.drawing.paint\nimport io.nacular.doodle.drawing.width\nimport io.nacular.doodle.geometry.Point.Companion.Origin\nimport io.nacular.doodle.geometry.Size\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.text.Target.Background\nimport io.nacular.doodle.text.TextDecoration\nimport io.nacular.doodle.text.TextDecoration.Line.Under\nimport io.nacular.doodle.text.TextDecoration.Style.Wavy\nimport io.nacular.doodle.text.TextDecoration.Thickness.Absolute\nimport io.nacular.doodle.text.TextSpacing\nimport io.nacular.doodle.text.invoke\nimport io.nacular.doodle.utils.Resizer\nimport kotlinx.coroutines.CoroutineScope\nimport kotlinx.coroutines.launch\n\nclass StyledTextApp(display: Display, fonts: FontLoader, appScope: CoroutineScope): Application {\n    init {\n        appScope.launch {\n            val bold = fonts {\n                family = "verdana"\n                weight = 700\n            }\n\n            display += view {\n//sampleStart\n                val decoration = TextDecoration(\n                    lines     = setOf(Under),\n                    color     = Red,\n                    thickness = Absolute(1.0),\n                    style     = Wavy\n                )\n\n                val text = bold("Lorem Ipsum").." is simply "..Yellow("dummy text", target = Background)..\n                        " of the printing and typesetting industry. It has been the industry\'s standard dummy text "..\n                        decoration("ever since the 1500s")..\n                        ", when an unknown printer took a galley of type and scrambled it to make a type specimen book."\n\n                render = {\n                    rect(bounds.atOrigin, fill = White.paint)\n\n                    wrapped(\n                        text        = text,\n                        at          = Origin,\n                        width       = this.width,\n                        textSpacing = TextSpacing(wordSpacing = 5.0, letterSpacing = 1.0),\n                        lineSpacing = 1.2f\n                    )\n                }\n//sampleEnd\n            }.apply {\n                size = Size(400, 200)\n                Resizer(this).apply { movable = false }\n            }\n\n            display.fill(controlBackgroundColor.paint)\n            display.layout = constrain(display.first()) {\n                it.width  lessEq parent.width - 20\n                it.center eq     parent.center\n            }\n        }\n    }\n\n    override fun shutdown() {}\n}',m='package rendering\n\nimport io.nacular.doodle.drawing.FontLoader\nimport io.nacular.doodle.scheduler.Scheduler\nimport io.nacular.measured.units.Time.Companion.seconds\nimport io.nacular.measured.units.times\nimport kotlinx.coroutines.coroutineScope\nimport kotlinx.coroutines.launch\n\nsuspend fun fontTimeout(fonts: FontLoader, scheduler: Scheduler) = coroutineScope {\n//sampleStart\n    // track loading job\n    val fontJob = launch {\n        // assigns the font when the job resolves\n        val font = fonts {\n            family = "Roboto"\n            size   = 14\n            weight = 400\n        }\n    }\n\n    // Cancel the job after 5 seconds\n    scheduler.after(5 * seconds) {\n        if (!fontJob.isCancelled) {\n            fontJob.cancel()\n        }\n    }\n//sampleEnd\n}',x='package rendering\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.application.application\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.core.view\nimport io.nacular.doodle.drawing.Color.Companion.Black\nimport io.nacular.doodle.drawing.TextMetrics\nimport io.nacular.doodle.drawing.paint\nimport io.nacular.doodle.geometry.Point\nimport org.kodein.di.instance\n\nclass MyApp(display: Display, textMetrics: TextMetrics): Application {\n    init {\n//sampleStart\n        display += view {\n            val hello    = "hello"\n            val textSize = textMetrics.size(hello) // cache text size\n\n            render = {\n                text(\n                    text = hello,\n                    at   = Point((width - textSize.width) / 2, (height - textSize.height) / 2),\n                    fill = Black.paint\n                )\n            }\n        }\n//sampleEnd\n    }\n    override fun shutdown() {}\n}\n\nfun launch() {\n    application {\n        // TextMetrics is available to inject by default\n        MyApp(display = instance(), textMetrics = instance())\n    }\n}',g='package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.core.view\nimport io.nacular.doodle.docs.utils.controlBackgroundColor\nimport io.nacular.doodle.drawing.Color.Companion.Black\nimport io.nacular.doodle.drawing.Color.Companion.White\nimport io.nacular.doodle.drawing.paint\nimport io.nacular.doodle.drawing.width\nimport io.nacular.doodle.geometry.Point.Companion.Origin\nimport io.nacular.doodle.geometry.Size\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.text.TextSpacing\nimport io.nacular.doodle.utils.Resizer\n\nclass MultiLinedTextApp(display: Display): Application {\n    init {\n        display += view {\n//sampleStart\n            val text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. It has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."\n\n            render = {\n                rect(bounds.atOrigin, fill = White.paint)\n\n                wrapped(\n                    text        = text,\n                    at          = Origin,\n                    width       = this.width,\n                    fill        = Black.paint,\n                    textSpacing = TextSpacing(wordSpacing = 5.0, letterSpacing = 1.0),\n                    lineSpacing = 1.2f\n                )\n            }\n//sampleEnd\n\n        }.apply {\n            size = Size(400, 200)\n            Resizer(this).apply { movable = false }\n        }\n\n        display.fill(controlBackgroundColor.paint)\n        display.layout = constrain(display.first()) {\n            it.width  lessEq parent.width - 20\n            it.center eq     parent.center\n        }\n    }\n\n    override fun shutdown() {}\n}',f={hide_title:!0,tile:"Text",sidebar_label:"Text"},y=void 0,w={id:"rendering/text",title:"text",description:"Displaying text",source:"@site/docs/rendering/text.mdx",sourceDirName:"rendering",slug:"/rendering/text",permalink:"/doodle/docs/rendering/text",draft:!1,unlisted:!1,tags:[],version:"current",frontMatter:{hide_title:!0,tile:"Text",sidebar_label:"Text"},sidebar:"tutorialSidebar",previous:{title:"Overview",permalink:"/doodle/docs/rendering/overview"},next:{title:"Images",permalink:"/doodle/docs/rendering/images"}},v={},k=[{value:"Displaying text",id:"displaying-text",level:2},{value:"Styled text",id:"styled-text",level:2},{value:"Measuring Text",id:"measuring-text",level:2},{value:"Fonts",id:"fonts",level:2},{value:"System Fonts",id:"system-fonts",level:2},{value:"Font Files",id:"font-files",level:2},{value:"Handling Timeouts",id:"handling-timeouts",level:2}];function j(n){const e={a:"a",admonition:"admonition",code:"code",h2:"h2",p:"p",...(0,i.M)(),...n.components};return l||b("api",!1),l.CanvasText||b("api.CanvasText",!0),l.CanvasWrapped||b("api.CanvasWrapped",!0),l.FontLoader||b("api.FontLoader",!0),l.FontModule||b("api.FontModule",!0),l.Paint||b("api.Paint",!0),l.StyledText||b("api.StyledText",!0),l.TextMetrics||b("api.TextMetrics",!0),(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(e.h2,{id:"displaying-text",children:"Displaying text"}),"\n",(0,t.jsxs)(e.p,{children:["All text, whether single or multi-lined, is drawn directly to a ",(0,t.jsx)(e.a,{href:"overview#the-canvas",children:"Canvas"})," using the ",(0,t.jsx)(l.CanvasText,{})," and ",(0,t.jsx)(l.CanvasWrapped,{})," methods. Text rendering is also explicit, with each API requiring a position within the ",(0,t.jsx)(e.code,{children:"Canvas"})," and the ",(0,t.jsx)(l.Paint,{}),' used for filling. The following View draw\'s "hello" at ',(0,t.jsx)(e.code,{children:"0,0"})," using the default font. But it is possible to change the font, letter, word and line spacing (for multi-lined text) as well."]}),"\n",(0,t.jsx)(a.A,{children:d}),"\n",(0,t.jsxs)(e.p,{children:["You can draw wrapped text using ",(0,t.jsx)(l.CanvasWrapped,{}),", which takes information about the width you'd like the text to occupy. Wrapped text also allows you to specify the line spacing, otherwise, it shares the same inputs as regular text."]}),"\n",(0,t.jsx)(r.u,{functionName:"multiLineText",height:"300"}),"\n",(0,t.jsx)(a.A,{children:g}),"\n",(0,t.jsx)(e.h2,{id:"styled-text",children:"Styled text"}),"\n",(0,t.jsxs)(e.p,{children:["You can also draw text (single and multi-line) that is styled using the ",(0,t.jsx)(l.StyledText,{})," class and its DSLs."]}),"\n",(0,t.jsx)(r.u,{functionName:"styledText",height:"300"}),"\n",(0,t.jsx)(a.A,{children:h}),"\n",(0,t.jsx)(e.h2,{id:"measuring-text",children:"Measuring Text"}),"\n",(0,t.jsxs)(e.p,{children:["All text is positioned explicitly when rendered to a ",(0,t.jsx)(e.code,{children:"Canvas"}),". This means text alignments like centering etc., require knowledge of the text's size. You can get this via a ",(0,t.jsx)(l.TextMetrics,{}),", which is available by default for injection into all apps."]}),"\n",(0,t.jsxs)(e.p,{children:["This examples shows a ",(0,t.jsx)(e.code,{children:"View"})," that draws some centered text based on the calculated size of the text."]}),"\n",(0,t.jsx)(a.A,{children:x}),"\n",(0,t.jsx)(e.admonition,{type:"tip",children:(0,t.jsx)(e.p,{children:"The text location could be computed only when the View's size changes, since render can be called even more frequently than that."})}),"\n",(0,t.jsx)(e.h2,{id:"fonts",children:"Fonts"}),"\n",(0,t.jsxs)(e.p,{children:["You can specify a font when drawing text or have Doodle fallback to the default. Fonts can be tricky, since they may not be present on the system at render time. This presents a race-condition for drawing text, since any text drawn with a Font that is simultaneously being loaded (or missing) can be shown in the wrong Font. This is what the ",(0,t.jsx)(l.FontLoader,{})," is designed to help with."]}),"\n",(0,t.jsx)(e.p,{children:"It presents an asynchronous API for fetching Fonts so the app is explicitly made to deal with this."}),"\n",(0,t.jsx)(s.cp,{link:(0,t.jsx)(l.FontModule,{}),module:u}),"\n",(0,t.jsx)(e.h2,{id:"system-fonts",children:"System Fonts"}),"\n",(0,t.jsxs)(e.p,{children:["You can use ",(0,t.jsx)(l.FontLoader,{})," to check the system asynchronously for a given font. This allows you to check for OS fonts, or fonts that have been loaded previously."]}),"\n",(0,t.jsx)(a.A,{children:p}),"\n",(0,t.jsx)(e.h2,{id:"font-files",children:"Font Files"}),"\n",(0,t.jsxs)(e.p,{children:["You can also load a font from a file or url using ",(0,t.jsx)(l.FontLoader,{}),". This is similar to finding a loaded font, but it takes a font file url."]}),"\n",(0,t.jsx)(a.A,{children:c}),"\n",(0,t.jsx)(e.h2,{id:"handling-timeouts",children:"Handling Timeouts"}),"\n",(0,t.jsxs)(e.p,{children:["The ",(0,t.jsx)(l.FontLoader,{})," uses Kotlin's ",(0,t.jsx)(e.code,{children:"suspend"})," functions for its async methods. ",(0,t.jsx)(e.a,{href:"https://kotlinlang.org/docs/reference/coroutines-overview.html",children:"Coroutines"})," are a flexible way of dealing with async/await semantics. You can support timeouts using ",(0,t.jsx)(e.code,{children:"launch"})," and canceling the resulting Job after some duration."]}),"\n",(0,t.jsx)(a.A,{children:m})]})}function S(n={}){const{wrapper:e}={...(0,i.M)(),...n.components};return e?(0,t.jsx)(e,{...n,children:(0,t.jsx)(j,{...n})}):j(n)}function b(n,e){throw new Error("Expected "+(e?"component":"object")+" `"+n+"` to be defined: you likely forgot to import, pass, or provide it.")}}}]);
"use strict";(self.webpackChunkdoodle_docs=self.webpackChunkdoodle_docs||[]).push([[9688],{9712:(e,n,o)=>{o.d(n,{SI:()=>r,cp:()=>d});var t=o(7624),i=o(4552),a=(o(7793),o(6236),o(7492));const r=[];function l(e){const n={admonition:"admonition",p:"p",...(0,i.M)(),...e.components};return(0,t.jsxs)(n.admonition,{title:"Module Required",type:"info",children:[(0,t.jsxs)("p",{children:["You must include the ",e.link," in your application in order to use these features."]}),(0,t.jsx)(a.A,{children:e.module}),(0,t.jsx)(n.p,{children:"Doodle uses opt-in modules like this to improve bundle size."})]})}function d(e={}){const{wrapper:n}={...(0,i.M)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(l,{...e})}):l(e)}},4:(e,n,o)=>{o.r(n),o.d(n,{assets:()=>f,contentTitle:()=>g,default:()=>y,frontMatter:()=>h,metadata:()=>v,toc:()=>x});var t=o(7624),i=o(4552),a=(o(7793),o(6236),o(9712)),r=o(5272),l=o(7492),d=o(3220);const s='package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.controls.IndexedItem\nimport io.nacular.doodle.controls.ItemVisualizer\nimport io.nacular.doodle.controls.MultiSelectionModel\nimport io.nacular.doodle.controls.list.List\nimport io.nacular.doodle.controls.mutableListModelOf\nimport io.nacular.doodle.controls.panels.ScrollPanel\nimport io.nacular.doodle.controls.table.DynamicTable\nimport io.nacular.doodle.controls.text.Label\nimport io.nacular.doodle.controls.toString\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.core.container\nimport io.nacular.doodle.datatransport.Files\nimport io.nacular.doodle.datatransport.Image\nimport io.nacular.doodle.datatransport.LocalFile\nimport io.nacular.doodle.datatransport.PlainText\nimport io.nacular.doodle.datatransport.TextType\nimport io.nacular.doodle.datatransport.dragdrop.DropEvent\nimport io.nacular.doodle.datatransport.dragdrop.DropReceiver\nimport io.nacular.doodle.docs.utils.DEFAULT_FONT_FAMILIES\nimport io.nacular.doodle.docs.utils.DEFAULT_FONT_SIZE\nimport io.nacular.doodle.drawing.Color.Companion.White\nimport io.nacular.doodle.drawing.FontLoader\nimport io.nacular.doodle.drawing.paint\nimport io.nacular.doodle.geometry.Rectangle\nimport io.nacular.doodle.geometry.Size\nimport io.nacular.doodle.layout.constraints.Bounds\nimport io.nacular.doodle.layout.constraints.ConstraintDslContext\nimport io.nacular.doodle.layout.constraints.center\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.layout.constraints.fill\nimport io.nacular.doodle.theme.Theme\nimport io.nacular.doodle.theme.ThemeManager\nimport io.nacular.doodle.utils.Direction\nimport io.nacular.doodle.utils.Resizer\nimport io.nacular.measured.units.BinarySize.Companion.kilobytes\nimport io.nacular.measured.units.times\nimport io.nacular.measured.units.toNearest\nimport kotlinx.coroutines.CoroutineScope\nimport kotlinx.coroutines.Dispatchers\nimport kotlinx.coroutines.SupervisorJob\nimport kotlinx.coroutines.launch\n\nclass FileDragApp(\n    display       : Display,\n    fonts         : FontLoader,\n    themeManager  : ThemeManager,\n    theme         : Theme,\n    textVisualizer: ItemVisualizer<String, IndexedItem>\n): Application {\n\n    private fun fileSize(file: LocalFile) = file.size toNearest 1 * kilobytes\n\n    init {\n        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {\n            val font = fonts {\n                families = DEFAULT_FONT_FAMILIES\n                size     = DEFAULT_FONT_SIZE\n            }\n\n            themeManager.selected = theme\n\n            val leftAligned: ConstraintDslContext.(Bounds) -> Unit = { it.left eq 4; it.centerY eq parent.centerY }\n\n            val fileTypes = listOf(\n                PlainText,\n                TextType("csv"),\n                Image("jpg"),\n                Image("jpeg")\n            )\n\n            val fileTypesList = List(fileTypes, toString(textVisualizer), MultiSelectionModel(), fitContent = emptySet()).apply {\n                this.font = font\n\n                cellAlignment = {\n                    it.left    eq 4\n                    it.centerY eq parent.centerY\n                }\n            }\n\n            val tableData = mutableListModelOf<LocalFile>()\n            val fileTable = DynamicTable(tableData, MultiSelectionModel()) {\n                column(Label("Name"), { name }, textVisualizer) { minWidth = 50.0; width = 200.0; headerAlignment = leftAligned; cellAlignment = leftAligned }\n                column(Label("Size"), { fileSize(this) }, toString(textVisualizer)) { minWidth = 50.0; width = 75.0; cellAlignment = { it.right eq parent.right - 4; it.centerY eq parent.centerY } }\n                column(Label("Type"), { type }, textVisualizer) { minWidth = 50.0; width = 150.0; cellAlignment = center }\n            }.apply {\n                var allowedFileTypes: Files? = null\n\n                fileTypesList.selectionChanged += { _, _, _ ->\n                    allowedFileTypes = Files(*fileTypesList.selection.map { fileTypes[it] }.toTypedArray())\n                }\n\n                this.font = font\n                bounds = Rectangle(200, 0, 400, 200)\n//sampleStart\n                dropReceiver = object: DropReceiver {\n                    override val active = true\n\n                    private  fun allowed          (event: DropEvent) = (allowedFileTypes?.let { it in event.bundle } ?: false).also { println("allowedFileTypes: ${allowedFileTypes?.types}") }\n                    override fun dropEnter        (event: DropEvent) = allowed(event)\n                    override fun dropOver         (event: DropEvent) = allowed(event)\n                    override fun dropActionChanged(event: DropEvent) = allowed(event)\n                    override fun drop             (event: DropEvent) = allowedFileTypes?.let { event.bundle[it] }?.let { files ->\n                        files.forEach { tableData.add(it) }\n                        true\n                    } ?: false\n                }\n//sampleEnd\n                Resizer(this).apply {\n                    movable = false\n                    directions = setOf(Direction.East)\n                }\n            }\n\n            display += container {\n                this += ScrollPanel(fileTypesList).apply {\n                    size                     = Size(140, 200)\n                    contentWidthConstraints  = { it eq max(parent.width,  it) }\n                    contentHeightConstraints = { it eq max(parent.height, it) }\n                }\n                this += fileTable\n\n                layout = constrain(children[0], children[1]) { a, b ->\n                    val spacing = 10\n\n                    a.left    eq (parent.width - (a.width.readOnly + b.width.readOnly + spacing)) / 2\n                    a.centerY eq parent.centerY\n                    b.left    eq a.right + spacing\n                    b.top     eq a.top\n                }\n            }\n\n            display.layout = constrain(display.first(), fill)\n            display.fill(White.paint)\n        }\n    }\n\n    override fun shutdown() {}\n}',p='package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.controls.buttons.PushButton\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.datatransport.dragdrop.DragOperation\nimport io.nacular.doodle.datatransport.dragdrop.DragOperation.Action\nimport io.nacular.doodle.datatransport.dragdrop.DragOperation.Action.Copy\nimport io.nacular.doodle.datatransport.dragdrop.DragOperation.Action.Move\nimport io.nacular.doodle.datatransport.dragdrop.dragRecognized\nimport io.nacular.doodle.datatransport.textBundle\nimport io.nacular.doodle.docs.utils.DEFAULT_FONT_FAMILIES\nimport io.nacular.doodle.docs.utils.DEFAULT_FONT_SIZE\nimport io.nacular.doodle.drawing.Canvas\nimport io.nacular.doodle.drawing.Color.Companion.Red\nimport io.nacular.doodle.drawing.Color.Companion.White\nimport io.nacular.doodle.drawing.FontLoader\nimport io.nacular.doodle.drawing.Renderable\nimport io.nacular.doodle.drawing.paint\nimport io.nacular.doodle.drawing.text\nimport io.nacular.doodle.geometry.Point\nimport io.nacular.doodle.geometry.Size\nimport io.nacular.doodle.layout.constraints.center\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.theme.Theme\nimport io.nacular.doodle.theme.ThemeManager\nimport kotlinx.coroutines.CoroutineScope\nimport kotlinx.coroutines.Dispatchers\nimport kotlinx.coroutines.SupervisorJob\nimport kotlinx.coroutines.launch\n\nclass ButtonDragApp(\n    display     : Display,\n    fonts       : FontLoader,\n    themeManager: ThemeManager,\n    theme       : Theme\n): Application {\n    init {\n        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {\n            val font = fonts {\n                families = DEFAULT_FONT_FAMILIES\n                size     = DEFAULT_FONT_SIZE\n            }\n\n            themeManager.selected = theme\n\n//sampleStart\n            val button = PushButton("Drag Me").apply {\n                this.font = font\n                size = Size(80, 40)\n\n                dragRecognizer = dragRecognized {\n                    object: DragOperation {\n                        override val bundle         = textBundle(text)\n                        override val allowedActions = setOf(Copy, Move)\n                        override val visualOffset   = Point(0, 14)\n                        override val visual         = object: Renderable {\n                            override val size = this@apply.size\n\n                            override fun render(canvas: Canvas) {\n                                canvas.text(text, font = font, color = Red)\n                            }\n                        }\n\n                        override fun completed(action: Action) {\n                            if (action == Move) text = ""\n                        }\n                    }\n                }\n            }\n//sampleEnd\n\n            display += button\n            display.layout = constrain(display.first(), center)\n            display.fill(White.paint)\n        }\n    }\n\n    override fun shutdown() {}\n}',c='package io.nacular.doodle.docs.apps\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.controls.buttons.PushButton\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.datatransport.PlainText\nimport io.nacular.doodle.datatransport.dragdrop.DropEvent\nimport io.nacular.doodle.datatransport.dragdrop.DropReceiver\nimport io.nacular.doodle.docs.utils.DEFAULT_FONT_FAMILIES\nimport io.nacular.doodle.docs.utils.DEFAULT_FONT_SIZE\nimport io.nacular.doodle.drawing.Color.Companion.White\nimport io.nacular.doodle.drawing.FontLoader\nimport io.nacular.doodle.drawing.paint\nimport io.nacular.doodle.geometry.Size\nimport io.nacular.doodle.layout.constraints.center\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.theme.Theme\nimport io.nacular.doodle.theme.ThemeManager\nimport kotlinx.coroutines.CoroutineScope\nimport kotlinx.coroutines.Dispatchers\nimport kotlinx.coroutines.SupervisorJob\nimport kotlinx.coroutines.launch\n\nclass ButtonDropApp(\n    display     : Display,\n    fonts       : FontLoader,\n    themeManager: ThemeManager,\n    theme       : Theme\n): Application {\n    init {\n        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {\n            val font = fonts {\n                families = DEFAULT_FONT_FAMILIES\n                size     = DEFAULT_FONT_SIZE\n            }\n\n            themeManager.selected = theme\n\n//sampleStart\n            val button = PushButton("_____").apply {\n                this.font = font\n                size = Size(80, 40)\n\n                dropReceiver = object: DropReceiver {\n                    override val active = true\n\n                    private  fun allowed          (event: DropEvent) = PlainText in event.bundle\n                    override fun dropEnter        (event: DropEvent) = allowed(event)\n                    override fun dropOver         (event: DropEvent) = allowed(event)\n                    override fun dropActionChanged(event: DropEvent) = allowed(event)\n                    override fun drop             (event: DropEvent) = event.bundle[PlainText]?.let { this@apply.text = it; true } ?: false\n                }\n            }\n//sampleEnd\n\n            display += button\n            display.layout = constrain(display.first(), center)\n            display.fill(White.paint)\n        }\n    }\n\n    override fun shutdown() {}\n}',u="package dragdrop\n\nimport io.nacular.doodle.application.Modules.Companion.DragDropModule\nimport io.nacular.doodle.application.application\nimport org.kodein.di.instance\nimport rendering.MyApp\n\nfun main() {\n//sampleStart\n    application(modules = listOf(DragDropModule)) {\n        MyApp(instance(), instance())\n    }\n//sampleEnd\n}",m='package dragdrop\n\nimport io.nacular.doodle.datatransport.dragdrop.DragOperation\nimport io.nacular.doodle.datatransport.dragdrop.DragRecognizer\nimport io.nacular.doodle.datatransport.dragdrop.dragRecognized\nimport io.nacular.doodle.event.PointerEvent\n\n//sampleStart\n// Interface approach\nclass Custom: DragRecognizer {\n    override fun dragRecognized(event: PointerEvent): DragOperation? {\n        TODO("Not yet implemented")\n    }\n}\n\n// DSL approach\nval recognizer = dragRecognized { _: PointerEvent ->\n    TODO("Not yet implemented")\n}\n//sampleEnd',h={hide_title:!0,title:"Drag \u2022 Drop"},g=void 0,v={id:"dragdrop",title:"Drag \u2022 Drop",description:"Drag and drop",source:"@site/docs/dragdrop.mdx",sourceDirName:".",slug:"/dragdrop",permalink:"/doodle/docs/dragdrop",draft:!1,unlisted:!1,tags:[],version:"current",frontMatter:{hide_title:!0,title:"Drag \u2022 Drop"},sidebar:"tutorialSidebar",previous:{title:"Animation",permalink:"/doodle/docs/animations"},next:{title:"Popups \u2022 Modals",permalink:"/doodle/docs/modals"}},f={},x=[{value:"Drag and drop",id:"drag-and-drop",level:2},...a.SI,{value:"Creating a source",id:"creating-a-source",level:2},{value:"Receiving drops",id:"receiving-drops",level:2},{value:"Data bundles",id:"data-bundles",level:2},{value:"Event Sequence",id:"event-sequence",level:2},{value:"Handling files",id:"handling-files",level:2}];function D(e){const n={admonition:"admonition",code:"code",h2:"h2",mermaid:"mermaid",p:"p",...(0,i.M)(),...e.components};return d.m||T("api",!1),d.m.DataBundle||T("api.DataBundle",!0),d.m.DragDropModule||T("api.DragDropModule",!0),d.m.DragOperation||T("api.DragOperation",!0),d.m.DragRecognized||T("api.DragRecognized",!0),d.m.DragRecognizer||T("api.DragRecognizer",!0),d.m.DropEvent||T("api.DropEvent",!0),d.m.DropReceiver||T("api.DropReceiver",!0),d.m.Files||T("api.Files",!0),d.m.MimeType||T("api.MimeType",!0),d.m.PlainText||T("api.PlainText",!0),d.m.PointerEvent||T("api.PointerEvent",!0),d.m.PointerModule||T("api.PointerModule",!0),d.m.View||T("api.View",!0),(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(n.h2,{id:"drag-and-drop",children:"Drag and drop"}),"\n",(0,t.jsx)(n.p,{children:"Drag-and-drop is a form of data transfer between two Views or a View and an external component. The Views involved can be within a single app, or separate apps; and the external component may be within a 3rd-party app entirely."}),"\n",(0,t.jsxs)(n.p,{children:["The key components involved in drag-and-drop sequence are: a ",(0,t.jsx)(d.m.DragRecognizer,{})," attached to a source View, ",(0,t.jsx)(d.m.DropReceiver,{})," linked to a receiver View, and a ",(0,t.jsx)(d.m.DataBundle,{}),". Both the source and/or target can be external to the app, meaning there might not be a recognizer or receiver at play."]}),"\n",(0,t.jsx)(n.p,{children:"The operation occurs when the user presses and drags a pointer within a drag-and-drop source, then drags that pointer onto a target and releases. Data will be captured from the source and provided to the target, where it can decide whether and how to accept it."}),"\n",(0,t.jsx)(a.cp,{link:(0,t.jsx)(d.m.DragDropModule,{}),module:u}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsxs)(n.p,{children:["The ",(0,t.jsx)(n.code,{children:"DragDropModule"})," imports the ",(0,t.jsx)(d.m.PointerModule,{})," as well, so you do not need to do so."]})}),"\n",(0,t.jsx)(n.h2,{id:"creating-a-source",children:"Creating a source"}),"\n",(0,t.jsxs)(n.p,{children:["You can create a drag source by attaching a ",(0,t.jsx)(d.m.DragRecognizer,{})," to any ",(0,t.jsx)(d.m.View,{}),". The recognizer is responsible for initiating the operation in response to a drag ",(0,t.jsx)(d.m.PointerEvent,{}),". You can create a custom recognizer that implements the interface, or you can use the ",(0,t.jsx)(d.m.DragRecognized,{})," DSL."]}),"\n",(0,t.jsx)(l.A,{children:m}),"\n",(0,t.jsxs)(n.p,{children:["The operation begins whenever a receiver returns a non-null ",(0,t.jsx)(d.m.DragOperation,{})," from ",(0,t.jsx)(d.m.DragRecognized,{}),". The ",(0,t.jsx)(n.code,{children:"DragOperation"})," contains the transfer data, user action (copy, move, link), and visual used to provide feedback during the operation. This object represents the entire lifecycle of the operation and is notified when dragging begins, completes, or is canceled. This allows the source to update based on the outcome of the operation."]}),"\n",(0,t.jsx)(l.A,{children:p}),"\n",(0,t.jsx)(r.u,{functionName:"buttonDrag",height:"100"}),"\n",(0,t.jsx)(n.p,{children:"This is an example of a simple recognizer that allows the text from a button to be copied or moved to a target."}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsxs)(n.p,{children:["Having the ",(0,t.jsx)(d.m.PointerEvent,{})," that triggered the drag lets a recognizer decide which subregion in a View a drag can happen from.\nIt can also produce different data from different regions in a single View."]})}),"\n",(0,t.jsx)(n.h2,{id:"receiving-drops",children:"Receiving drops"}),"\n",(0,t.jsxs)(n.p,{children:["You receive drops by attaching a ",(0,t.jsx)(d.m.DropReceiver,{})," to any ",(0,t.jsx)(d.m.View,{}),". Dragging a pointer over a ",(0,t.jsx)(n.code,{children:"View"})," with a receiver during a drag-and-rop triggers the ",(0,t.jsx)(n.code,{children:"dropEnter"})," event. Subsequent pointer movement results in ",(0,t.jsx)(n.code,{children:"dropOver"})," or ",(0,t.jsx)(n.code,{children:"dropExit"})," events; and releasing the pointer sends the ",(0,t.jsx)(n.code,{children:"drop"})," event. Each of these events provides a ",(0,t.jsx)(d.m.DropEvent,{})," with information about the data being transferred and the user's intended action."]}),"\n",(0,t.jsxs)(n.p,{children:["The ",(0,t.jsx)(n.code,{children:"DropReceiver"})," indicates whether the current drop operation is allowed by returning ",(0,t.jsx)(n.code,{children:"true"})," on any of these events. Or, it\ncan return ",(0,t.jsx)(n.code,{children:"false"})," to signal the drop is not allowed."]}),"\n",(0,t.jsx)(r.u,{functionName:"buttonDrop",height:"100"}),"\n",(0,t.jsx)(l.A,{children:c}),"\n",(0,t.jsxs)(n.p,{children:["This is a simple receiver that accepts ",(0,t.jsx)(d.m.PlainText,{})," data and assigns it to the button's text. Try dragging some text onto the button. You can even drag from the previous app's button."]}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsxs)(n.p,{children:["A ",(0,t.jsx)(n.code,{children:"View"})," can be a source and target for drag-and-drop simultaneously."]})}),"\n",(0,t.jsx)(n.h2,{id:"data-bundles",children:"Data bundles"}),"\n",(0,t.jsxs)(n.p,{children:["The ",(0,t.jsx)(n.code,{children:"DataBundle"})," class manages the underlying data that is transferred between the source and target. This interface has two key methods: ",(0,t.jsx)(n.code,{children:"contains"})," and ",(0,t.jsx)(n.code,{children:"get"}),": both take a ",(0,t.jsx)(d.m.MimeType,{}),"."]}),"\n",(0,t.jsx)(l.A,{children:"\ninterface DataBundle {\n    operator fun <T> get     (type: MimeType<T>): T?\n    operator fun <T> contains(type: MimeType<T>): Boolean\n\n\n    //...\n}\n"}),"\n",(0,t.jsxs)(n.p,{children:["The ",(0,t.jsx)(n.code,{children:"contains"})," method checks whether the bundle has data that matches the given mime-type, and ",(0,t.jsx)(n.code,{children:"get"})," returns it."]}),"\n",(0,t.jsx)(n.h2,{id:"event-sequence",children:"Event Sequence"}),"\n",(0,t.jsx)(n.p,{children:"The full sequence of events for an operation goes something like this:"}),"\n",(0,t.jsx)(n.mermaid,{value:"sequenceDiagram\n    autonumber\n    participant s as Framework\n    participant r as DragRecognizer\n    participant o as DragOperation\n    participant b as DataBundle\n    participant p as DropReceiver\n\n    s->>r : dragRecognized\n    r--\x3e>s: DragOperation\n    s->>o : bundle, allowed actions, visual, ...\n    s->>p : dropEnter\n    loop\n        s->>p : dropOver, [dropExit]\n    end\n    s->>p : drop\n    p->>b : contains, invoke\n    b--\x3e>p: T\n    s->>o : completed"}),"\n",(0,t.jsx)(n.h2,{id:"handling-files",children:"Handling files"}),"\n",(0,t.jsxs)(n.p,{children:["Drag-and-drop also supports file transfer. This is handled via a ",(0,t.jsx)(d.m.DropReceiver,{}),"--just like any other data type, with the ",(0,t.jsx)(d.m.Files,{})," mime-type indicating which file types are allowed."]}),"\n",(0,t.jsxs)(n.p,{children:["Try dragging files into the table below. The app will only allow files whose types are selected in the list. The ",(0,t.jsx)(n.code,{children:"Files"})," mime-type fetches a collection of files from the bundle, which allows a receiver to handle multiple files in a single drop."]}),"\n",(0,t.jsx)(r.u,{functionName:"fileDragDrop",height:"300"}),"\n",(0,t.jsx)(l.A,{children:s})]})}function y(e={}){const{wrapper:n}={...(0,i.M)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(D,{...e})}):D(e)}function T(e,n){throw new Error("Expected "+(n?"component":"object")+" `"+e+"` to be defined: you likely forgot to import, pass, or provide it.")}}}]);
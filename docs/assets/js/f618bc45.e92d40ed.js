"use strict";(self.webpackChunkdoodle_docs=self.webpackChunkdoodle_docs||[]).push([[615],{929:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>c,contentTitle:()=>d,default:()=>m,frontMatter:()=>l,metadata:()=>u,toc:()=>h});var a=t(7462),i=(t(7294),t(3905)),o=(t(8209),t(5488)),s=t(5162),r=t(4903);const l={hide_title:!0},d="Using Layouts",u={unversionedId:"layout/layouts",id:"layout/layouts",title:"Using Layouts",description:"A Layout keeps track",source:"@site/docs/layout/layouts.mdx",sourceDirName:"layout",slug:"/layout/layouts",permalink:"/doodle/docs/layout/layouts",draft:!1,tags:[],version:"current",frontMatter:{hide_title:!0},sidebar:"tutorialSidebar",previous:{title:"Positioning",permalink:"/doodle/docs/layout/overview"},next:{title:"Constraints",permalink:"/doodle/docs/layout/constraints"}},c={},h=[{value:"Custom Layouts",id:"custom-layouts",level:2},{value:"Deciding When Layout Happens",id:"deciding-when-layout-happens",level:2}],p={toc:h};function m(e){let{components:n,...t}=e;return(0,i.kt)("wrapper",(0,a.Z)({},p,t,{components:n,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"using-layouts"},"Using Layouts"),(0,i.kt)("p",null,"A ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/Layout.kt#L80"},(0,i.kt)("inlineCode",{parentName:"a"},"Layout"))," keeps track\nof a View and its children and automatically arranges the children as sizes change. This happens (by default) whenever View's ",(0,i.kt)("inlineCode",{parentName:"p"},"size")," changes, or one of its children has its ",(0,i.kt)("inlineCode",{parentName:"p"},"bounds")," change."),(0,i.kt)("p",null,"The View class also ",(0,i.kt)("inlineCode",{parentName:"p"},"protects")," its ",(0,i.kt)("inlineCode",{parentName:"p"},"layout")," property from callers, but sub-classes are free to expose\nit."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"val container = container {}\n\ncontainer.layout = HorizontalFlowLayout() // Container exposes its layout\n")),(0,i.kt)(r.l,{functionName:"flowLayout",height:"400",mdxType:"DoodleApp"}),(0,i.kt)("p",null,(0,i.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/layout/HorizontalFlowLayout.kt#L16"},(0,i.kt)("inlineCode",{parentName:"a"},"HorizontalFlowLayout")),"\nwraps a View's children from left to right within its bounds."),(0,i.kt)("admonition",{type:"caution"},(0,i.kt)("p",{parentName:"admonition"},"Changes to a View's ",(0,i.kt)("inlineCode",{parentName:"p"},"transform")," will not trigger layout.")),(0,i.kt)("h2",{id:"custom-layouts"},"Custom Layouts"),(0,i.kt)("p",null,"Doodle comes with several useful layouts, including one based on constraints. You can also create custom Layouts very easily.\nJust implement the ",(0,i.kt)("inlineCode",{parentName:"p"},"Layout")," interface:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"class CustomLayout: Layout {\n    override fun layout(container: PositionableContainer) {\n        container.children.filter { it.visible }.forEach { child ->\n            child.bounds = Rectangle(/*...*/)\n        }\n    }\n}\n")),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},"Layouts do not work with View directly because it does not expose its children. ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/Layout.kt#L33"},(0,i.kt)("inlineCode",{parentName:"a"},"PositionableContainer"))," proxies the\nmanaged View instead.")),(0,i.kt)("h2",{id:"deciding-when-layout-happens"},"Deciding When Layout Happens"),(0,i.kt)("p",null,"Layouts are generally triggered whenever their container's size changes or a child of their container has a bounds change. But there are cases\nwhen this default behavior does not work as well. A good example is a Layout that uses a child's ",(0,i.kt)("inlineCode",{parentName:"p"},"idealSize")," in positioning. Such a Layout\nwon't be invoked when the ",(0,i.kt)("inlineCode",{parentName:"p"},"idealSize"),"s change, and will be out of date in some cases. The following demo shows this."),(0,i.kt)(o.Z,{mdxType:"Tabs"},(0,i.kt)(s.Z,{value:"demo",label:"Demo",mdxType:"TabItem"},(0,i.kt)(r.l,{functionName:"layoutIdealIssue",height:"400",mdxType:"DoodleApp"})),(0,i.kt)(s.Z,{value:"usage",label:"Usage",mdxType:"TabItem"},(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"container {\n    repeat(2) {\n        this += BlueView().apply { size = Size(50) }\n    }\n\n    // This Layout does not override\n    // requiresLayout(child: Positionable,\n    //       of: PositionableContainer,\n    //       old: SizePreferences,\n    //       new: SizePreferences\n    // ): Boolean\n    // Which means it defaults to ignoring changes to child SizePreferences\n    layout = object: Layout {\n        override fun layout(container: PositionableContainer) {\n            var x = 0.0\n            container.children.forEach {\n                it.x = x\n                x += (it.idealSize?.width ?: it.width) + 1\n            }\n        }\n    }\n\n    size   = Size(200)\n    render = {\n        rect(bounds.atOrigin, Lightgray.lighter())\n    }\n\n    Resizer(this)\n}\n")))),(0,i.kt)("admonition",{type:"info"},(0,i.kt)("p",{parentName:"admonition"},"Moving the slider changes the ideal width of the blue boxes. But the container isn't udpated because the Layout used does not indicate\nit needs an updated when ",(0,i.kt)("inlineCode",{parentName:"p"},"sizePreferences")," change via ",(0,i.kt)("inlineCode",{parentName:"p"},"requiresLayout"),"."),(0,i.kt)("p",{parentName:"admonition"},"You can see that it is out of date by resizing the container after moving the slider.")),(0,i.kt)("p",null,"This is why Doodle offers Layouts a chance to customize when they are invoked. In fact, Layouts are asked whether they want to respond to\nseveral potential triggers. These include size changes in the container, bounds and size preference changes for children. The latter happens\nwhenever ",(0,i.kt)("inlineCode",{parentName:"p"},"minimumSize")," or ",(0,i.kt)("inlineCode",{parentName:"p"},"idealSize")," are updated for a child. This way, a Layout can fine tune what triggers it."),(0,i.kt)("p",null,"The following shows how updating the Layout so it replies to ",(0,i.kt)("inlineCode",{parentName:"p"},"requiresLayout")," for this scenario fixes the issue."),(0,i.kt)(o.Z,{mdxType:"Tabs"},(0,i.kt)(s.Z,{value:"demo",label:"Demo",mdxType:"TabItem"},(0,i.kt)(r.l,{functionName:"layoutIdealIssue",args:"[true]",height:"400",mdxType:"DoodleApp"})),(0,i.kt)(s.Z,{value:"usage",label:"Usage",mdxType:"TabItem"},(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"container {\n    repeat(2) {\n        this += BlueView().apply { size = Size(50) }\n    }\n\n    layout = object: Layout {\n        // Request layout whenever a child's idealSize changes\n        // (and the updateOnIdealChange switch is tuned on)\n        override fun requiresLayout(\n            child: Positionable,\n            of  : PositionableContainer,\n            old : View.SizePreferences,\n            new : View.SizePreferences\n        ) = updateOnIdealChange && old.idealSize != new.idealSize\n\n        // This Layout is very unusual (b/c it is contrived) in that it does not depend\n        // on the container's size. So it ignores these changes.\n        override fun requiresLayout(container: PositionableContainer, old: Size, new: Size) = false\n\n        override fun layout(container: PositionableContainer) {\n            var x = 0.0\n            container.children.forEach {\n                it.x = x\n                x += (it.idealSize?.width ?: it.width) + 1\n            }\n        }\n    }\n\n    size   = Size(200)\n    render = {\n        rect(bounds.atOrigin, Lightgray.lighter())\n    }\n\n    Resizer(this)\n}\n")))),(0,i.kt)("admonition",{type:"info"},(0,i.kt)("p",{parentName:"admonition"},"Notice that this Layout will actually ignore changes to the container's ",(0,i.kt)("inlineCode",{parentName:"p"},"size"),"! Layouts are free to do that if the\ncontainer's ",(0,i.kt)("inlineCode",{parentName:"p"},"size")," is irrelevant to the positioning of its children. This is very unlikely, but there might be\ncases where one dimension of ",(0,i.kt)("inlineCode",{parentName:"p"},"size"),", maybe ",(0,i.kt)("inlineCode",{parentName:"p"},"width")," or ",(0,i.kt)("inlineCode",{parentName:"p"},"height")," is irrelevant. In which case the Layout can\nignore updates if only that component changes.")))}m.isMDXComponent=!0}}]);
"use strict";(self.webpackChunkdoodle_docs=self.webpackChunkdoodle_docs||[]).push([[862],{157:(e,o,t)=>{t.r(o),t.d(o,{assets:()=>d,contentTitle:()=>s,default:()=>c,frontMatter:()=>r,metadata:()=>l,toc:()=>p});var n=t(7462),a=(t(7294),t(3905)),i=(t(8209),t(5488),t(5162),t(4903));const r={hide_title:!0},s="Positioning",l={unversionedId:"layout/overview",id:"layout/overview",title:"Positioning",description:"Every View has an x, y position (in pixels) relative to its parent. This is exactly where the View will be rendered--unless it (or an ancestor) also has",source:"@site/docs/layout/overview.mdx",sourceDirName:"layout",slug:"/layout/overview",permalink:"/doodle/docs/layout/overview",draft:!1,tags:[],version:"current",frontMatter:{hide_title:!0},sidebar:"tutorialSidebar",previous:{title:"Where's My View?",permalink:"/doodle/docs/display/gotchas"},next:{title:"Using Layouts",permalink:"/doodle/docs/layout/layouts"}},d={},p=[{value:"Manual positioning",id:"manual-positioning",level:2},{value:"Transforms",id:"transforms",level:2}],m={toc:p};function c(e){let{components:o,...t}=e;return(0,a.kt)("wrapper",(0,n.Z)({},m,t,{components:o,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"positioning"},"Positioning"),(0,a.kt)("p",null,"Every View has an ",(0,a.kt)("inlineCode",{parentName:"p"},"x"),", ",(0,a.kt)("inlineCode",{parentName:"p"},"y")," position (in pixels) relative to its parent. This is exactly where the View will be rendered--unless it (or an ancestor) also has\na ",(0,a.kt)("inlineCode",{parentName:"p"},"transform"),". Doodle ensures that there is never a disconnect between a View's position, transform and render coordinates."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"val panel = view { size = Size(100.0) }\n\ndisplay += view // view's position is 0,0\n")),(0,a.kt)("h2",{id:"manual-positioning"},"Manual positioning"),(0,a.kt)("p",null,"You can set the View's ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/View.kt#L165"},(0,a.kt)("inlineCode",{parentName:"a"},"x")),",\n",(0,a.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/View.kt#L170"},(0,a.kt)("inlineCode",{parentName:"a"},"y")),", or\n",(0,a.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/View.kt#L175"},(0,a.kt)("inlineCode",{parentName:"a"},"position"))," properties directly\nto move it around. These are proxies to the View's ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/View.kt#L198"},(0,a.kt)("inlineCode",{parentName:"a"},"bounds")),"\nproperty, which represents its rectangular boundary relative to its parent."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"view.x = 10.0                 // move to 10,0\nview.position = Point(13, -2) // reposition to 13,-2\n")),(0,a.kt)(i.l,{functionName:"positioning",height:"400",mdxType:"DoodleApp"}),(0,a.kt)("p",null,"This demo shows how the pointer can be used to position Views easily. In this case, we use the ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/utils/Resizer.kt#L31"},"Resizer"),"\nutility to provide simple resize/move operations. The Resizer simply monitors the View for Pointer events and updates its ",(0,a.kt)("inlineCode",{parentName:"p"},"bounds")," accordingly."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"import io.nacular.doodle.utils.Resizer\n\n// ...\n\nMyView().apply {\n    bounds = Rectangle(100, 100)\n    Resizer(this) // monitors the View and manages resize/move\n}\n")),(0,a.kt)("h2",{id:"transforms"},"Transforms"),(0,a.kt)("p",null,"Views can also have\n",(0,a.kt)("a",{parentName:"p",href:"/doodle/docs/transforms"},"transformations")," to change how they are displayed. A transformed View still\nretains the same ",(0,a.kt)("inlineCode",{parentName:"p"},"bounds"),", but its ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/View.kt#L241"},(0,a.kt)("inlineCode",{parentName:"a"},"boundingBox"))," property changes, since it reflects the smallest rectangle that encloses the View's\n",(0,a.kt)("strong",{parentName:"p"},"transformed")," bounds."),(0,a.kt)(i.l,{functionName:"transforms",height:"400",mdxType:"DoodleApp"}),(0,a.kt)("admonition",{type:"tip"},(0,a.kt)("p",{parentName:"admonition"},(0,a.kt)("inlineCode",{parentName:"p"},"boundingBox")," == ",(0,a.kt)("inlineCode",{parentName:"p"},"bounds")," when\n",(0,a.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/View.kt#L231"},(0,a.kt)("inlineCode",{parentName:"a"},"transform"))," ==\n",(0,a.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/drawing/AffineTransform.kt#L272"},(0,a.kt)("inlineCode",{parentName:"a"},"Identity")),".")))}c.isMDXComponent=!0}}]);
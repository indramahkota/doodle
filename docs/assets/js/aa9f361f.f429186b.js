"use strict";(self.webpackChunkdoodle_docs=self.webpackChunkdoodle_docs||[]).push([[6728],{9628:(o,e,r)=>{r.r(e),r.d(e,{assets:()=>h,contentTitle:()=>a,default:()=>f,frontMatter:()=>d,metadata:()=>c,toc:()=>p});var n=r(7624),i=r(4552),t=r(7492),l=r(3220);const s="package rendering\n\nimport io.nacular.doodle.drawing.Color\nimport io.nacular.doodle.drawing.darker\nimport io.nacular.doodle.drawing.opacity\nimport io.nacular.doodle.drawing.paint\n\nfun colors() {\n//sampleStart\n    Color(0xff0000u                ) // create red\n    Color(0xffffffu, opacity = 0.5f) // white with 0.5 opacity\n    Color.Red                        // build-in red\n    Color.Blue opacity 0.5f          // blue with 0.5 opacity\n    Color.Blue.inverted              // inverse of blue\n    Color.Lightgray.darker(0.5f)     // darker gray\n    Color.Blue.paint                 // paint from a color\n//sampleEnd\n}",d={title:"Colors",hide_title:!0},a=void 0,c={id:"rendering/colors",title:"Colors",description:"Colors",source:"@site/docs/rendering/colors.mdx",sourceDirName:"rendering",slug:"/rendering/colors",permalink:"/doodle/docs/rendering/colors",draft:!1,unlisted:!1,tags:[],version:"current",frontMatter:{title:"Colors",hide_title:!0},sidebar:"tutorialSidebar",previous:{title:"Images",permalink:"/doodle/docs/rendering/images"},next:{title:"3D",permalink:"/doodle/docs/rendering/3d"}},h={},p=[{value:"Colors",id:"colors",level:2},{value:"HSL and HSV Colors",id:"hsl-and-hsv-colors",level:2}];function u(o){const e={a:"a",code:"code",h2:"h2",p:"p",...(0,i.M)(),...o.components};return l.m||m("api",!1),l.m.Color||m("api.Color",!0),l.m.ColorPaint||m("api.ColorPaint",!0),(0,n.jsxs)(n.Fragment,{children:[(0,n.jsx)(e.h2,{id:"colors",children:"Colors"}),"\n",(0,n.jsxs)(e.p,{children:["Doodle supports RGBA colors (via the ",(0,n.jsx)(l.m.Color,{})," class), for rendering content to the ",(0,n.jsx)(e.code,{children:"Canvas"})," via paints like ",(0,n.jsx)(l.m.ColorPaint,{}),". Colors can be created explicitly using hex notation, or you can use any of the built-in values (i.e. ",(0,n.jsx)(e.code,{children:"Red"}),"). Colors can also be derived from others with one of the many utility functions like ",(0,n.jsx)(e.code,{children:"lighter"}),", ",(0,n.jsx)(e.code,{children:"darker"}),", or by changing ",(0,n.jsx)(e.code,{children:"opacity"}),"."]}),"\n",(0,n.jsx)(t.A,{children:s}),"\n",(0,n.jsx)(e.h2,{id:"hsl-and-hsv-colors",children:"HSL and HSV Colors"}),"\n",(0,n.jsx)(e.p,{children:"Sometimes it is more effective to work within other color spaces. Doodle has built-in utilities for HSL and HSV colors, though these colors cannot be used directly for rendering. That said, there are many utilities for easily transforming between them and RGBA."}),"\n",(0,n.jsxs)(e.p,{children:["The ",(0,n.jsx)(e.a,{href:"../ui_components/overview#colorpicker",children:"ColorPicker"})," controls uses HSV internally for example."]})]})}function f(o={}){const{wrapper:e}={...(0,i.M)(),...o.components};return e?(0,n.jsx)(e,{...o,children:(0,n.jsx)(u,{...o})}):u(o)}function m(o,e){throw new Error("Expected "+(e?"component":"object")+" `"+o+"` to be defined: you likely forgot to import, pass, or provide it.")}}}]);
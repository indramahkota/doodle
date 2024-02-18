"use strict";(self.webpackChunkdoodle_docs=self.webpackChunkdoodle_docs||[]).push([[7844],{8488:(n,t,e)=>{e.r(t),e.d(t,{assets:()=>w,contentTitle:()=>p,default:()=>g,frontMatter:()=>h,metadata:()=>u,toc:()=>y});var i=e(7624),o=e(2172),a=(e(1268),e(5388),e(7996)),s=e(5720),r=e(3148);const l='package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.docs.utils.Panel\nimport io.nacular.doodle.drawing.Color.Companion.Gray\nimport io.nacular.doodle.drawing.Color.Companion.Lightgray\nimport io.nacular.doodle.drawing.TextMetrics\nimport io.nacular.doodle.layout.constraints.constrain\n\nclass ConstraintCreationApp(display: Display, textMetrics: TextMetrics): Application {\n    init {\n        val panel1 = Panel(textMetrics, "Panel 1").apply { backgroundColor = Lightgray }\n        val panel2 = Panel(textMetrics, "Panel 2").apply { backgroundColor = Gray      }\n\n        display += listOf(panel1, panel2)\n\n        // use Layout that follows constraints to position items\n//sampleStart\n        display.layout = constrain(panel1, panel2) { panel1, panel2 ->\n            panel1.top    eq 0\n            panel1.left   eq 0\n            panel1.right  eq parent.right / 3\n            panel1.bottom eq parent.bottom\n\n            panel2.top    eq panel1.top\n            panel2.left   eq panel1.right\n            panel2.right  eq parent.right\n            panel2.bottom eq parent.bottom\n        }\n//sampleEnd\n    }\n\n    override fun shutdown() {}\n}',d='package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.core.container\nimport io.nacular.doodle.core.width\nimport io.nacular.doodle.docs.utils.Panel\nimport io.nacular.doodle.drawing.Canvas\nimport io.nacular.doodle.drawing.Color.Companion.Black\nimport io.nacular.doodle.drawing.Color.Companion.Gray\nimport io.nacular.doodle.drawing.Color.Companion.Lightgray\nimport io.nacular.doodle.drawing.Color.Companion.White\nimport io.nacular.doodle.drawing.Stroke\nimport io.nacular.doodle.drawing.TextMetrics\nimport io.nacular.doodle.drawing.paint\nimport io.nacular.doodle.geometry.Size\nimport io.nacular.doodle.layout.constraints.Strength.Companion.Strong\nimport io.nacular.doodle.layout.constraints.center\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.utils.Resizer\nimport kotlin.math.min\n\nclass ConstraintStrengthApp(display: Display, textMetrics: TextMetrics): Application {\n    init {\n        val panel = object: Panel(textMetrics, "Panel") {\n            init {\n                backgroundColor = Lightgray\n            }\n\n            override fun render(canvas: Canvas) {\n                super.render(canvas)\n                canvas.rect(bounds.atOrigin, Stroke(Black, 2.0))\n            }\n        }\n\n        display += container {\n            this += panel\n\n            size = Size(min(400.0, display.width - 20), min(300.0, display.width - 20))\n\n//sampleStart\n            layout = constrain(panel) {\n                it.left   eq     0\n                it.width  lessEq 200\n                (it.right eq     parent.right) .. Strong // ignored when conflicts with above constraint\n                it.height eq     parent.height\n            }\n//sampleEnd\n\n            render = { rect(bounds.atOrigin, fill = Gray.paint) }\n\n            // helper to resize container\n            Resizer(this).apply { movable = false }\n        }\n\n        // use Layout that follows constraints to position items\n        display.layout = constrain(display.first(), center)\n\n        display.fill(White.paint)\n    }\n\n    override fun shutdown() {}\n}',c='package io.nacular.doodle.docs.apps\n\nimport io.nacular.doodle.application.Application\nimport io.nacular.doodle.core.Display\nimport io.nacular.doodle.core.container\nimport io.nacular.doodle.core.width\nimport io.nacular.doodle.docs.utils.BlueColor\nimport io.nacular.doodle.docs.utils.Panel\nimport io.nacular.doodle.drawing.Color.Companion.Gray\nimport io.nacular.doodle.drawing.Color.Companion.Lightgray\nimport io.nacular.doodle.drawing.Color.Companion.Pink\nimport io.nacular.doodle.drawing.Color.Companion.White\nimport io.nacular.doodle.drawing.TextMetrics\nimport io.nacular.doodle.drawing.paint\nimport io.nacular.doodle.geometry.Size\nimport io.nacular.doodle.layout.constraints.Strength.Companion.Weak\nimport io.nacular.doodle.layout.constraints.center\nimport io.nacular.doodle.layout.constraints.constrain\nimport io.nacular.doodle.utils.Direction.East\nimport io.nacular.doodle.utils.Direction.West\nimport io.nacular.doodle.utils.Resizer\nimport kotlin.math.min\n\nclass ComplexConstraintsApp(display: Display, textMetrics: TextMetrics): Application {\n    init {\n        val panel1 = Panel(textMetrics, "1").apply { backgroundColor = Pink; width = 350.0; Resizer(this).apply { directions = setOf(East      ); movable = false } }\n        val panel2 = Panel(textMetrics, "2").apply { backgroundColor = Gray; width =  50.0; Resizer(this).apply { directions = setOf(East, West)                  } }\n        val panel3 = Panel(textMetrics, "3").apply { backgroundColor = Lightgray;           Resizer(this).apply { directions = setOf(West      ); movable = false } }\n\n        display += container {\n            this += listOf(panel1, panel2, panel3)\n\n            size = Size(min(400.0, display.width - 20), min(300.0, display.width - 20))\n\n            val inset = 5\n\n//sampleStart\n            layout = constrain(panel1, panel2, panel3) { p1, p2, p3 ->\n                p1.top    eq     inset\n                p1.left   eq     inset\n                p1.width  lessEq (parent.width - inset) / 3\n                (p1.width eq     350) .. Weak\n                p1.height eq     parent.height - 2 * inset\n\n                p2.top    eq p1.top\n                p2.left   eq p1.right\n                p2.height eq p1.height\n\n                p3.top    eq p2.top\n                p3.left   eq p2.right\n                p3.height eq p2.height\n\n                p1.width + p2.width + p3.width eq parent.width - 2 * inset\n            }\n//sampleEnd\n\n            render = { rect(bounds.atOrigin, fill = BlueColor.paint) }\n\n            // helper to resize container\n            Resizer(this).apply { movable = false }\n        }\n\n        // use Layout that follows constraints to position items\n        display.layout = constrain(display.first(), center)\n\n        display.fill(White.paint)\n    }\n\n    override fun shutdown() {}\n}',h={title:"Constraints",hide_title:!0},p=void 0,u={id:"layouts/constraints",title:"Constraints",description:"Constraint layouts",source:"@site/docs/layouts/constraints.mdx",sourceDirName:"layouts",slug:"/layouts/constraints",permalink:"/doodle/docs/layouts/constraints",draft:!1,unlisted:!1,tags:[],version:"current",frontMatter:{title:"Constraints",hide_title:!0},sidebar:"tutorialSidebar",previous:{title:"Using Layouts",permalink:"/doodle/docs/layouts/"},next:{title:"Transforms",permalink:"/doodle/docs/transforms"}},w={},y=[{value:"Constraint layouts",id:"constraint-layouts",level:2},{value:"Creating constraints",id:"creating-constraints",level:2},{value:"Readonly attributes",id:"readonly-attributes",level:2},{value:"Constraint strength",id:"constraint-strength",level:2},{value:"Parents are read-only by default",id:"parents-are-read-only-by-default",level:2},{value:"Non-siblings constraints",id:"non-siblings-constraints",level:2},{value:"Constraints are live",id:"constraints-are-live",level:2},{value:"Removing constraints",id:"removing-constraints",level:2}];function m(n){const t={admonition:"admonition",code:"code",h2:"h2",p:"p",strong:"strong",...(0,o.M)(),...n.components};return r||v("api",!1),r.Bounds||v("api.Bounds",!0),r.Constrain||v("api.Constrain",!0),r.Strength||v("api.Strength",!0),r.Unconstrain||v("api.Unconstrain",!0),r.View||v("api.View",!0),(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(t.h2,{id:"constraint-layouts",children:"Constraint layouts"}),"\n",(0,i.jsxs)(t.p,{children:["Doodle also supports a constraints based layout that uses linear equations to define placement. This approach lets you write equations that define how several anchor points on a ",(0,i.jsx)(r.View,{})," (based on a provided ",(0,i.jsx)(r.Bounds,{}),") will be placed relative to other Views and the ",(0,i.jsx)(t.code,{children:"parent"})," View. This covers many of the common layout use cases and is easy to use."]}),"\n",(0,i.jsxs)(t.p,{children:["Each linear equation is one of the following forms. Where ",(0,i.jsx)(t.code,{children:"attribute"})," refers to anchor points or ",(0,i.jsx)(t.code,{children:"width"})," and ",(0,i.jsx)(t.code,{children:"height"}),"."]}),"\n",(0,i.jsx)(s.A,{children:"\nview1.attribute eq        constant1 * view2.attribute + constant2\nview1.attribute lessEq    constant1 * view2.attribute + constant2\nview1.attribute greaterEq constant1 * view2.attribute + constant2\n"}),"\n",(0,i.jsxs)(t.p,{children:["The Constraint system will modify all the attributes provided to it to ensure every equation (or inequality) is satisfied. In the above example, that means updating widths fo ",(0,i.jsx)(t.code,{children:"view1"})," and ",(0,i.jsx)(t.code,{children:"view2"})," to ensure they add up to the ",(0,i.jsx)(t.code,{children:"parent"})," width minus 10."]}),"\n",(0,i.jsxs)(t.admonition,{type:"tip",children:[(0,i.jsx)(t.p,{children:"You can also include multiple Views in a single equation:"}),(0,i.jsx)(s.A,{children:"\nview1.width + 2 * view2.width eq parent.width - 10\n"}),(0,i.jsx)(t.p,{children:"The fact that these are equations (or inequalities) means you can flip the order of attributes and have the same effect, as long as you obey the rules of mathematics and change signs accordingly. So the equation above is the same as:"}),(0,i.jsx)(s.A,{children:"\nview1.width + 10 eq parent.width - 2 * view2.width\n"})]}),"\n",(0,i.jsx)(t.h2,{id:"creating-constraints",children:"Creating constraints"}),"\n",(0,i.jsxs)(t.p,{children:["Constraint layouts are created using the ",(0,i.jsx)(t.code,{children:"constrain"})," function. This function takes a list of ",(0,i.jsx)(t.code,{children:"View"}),"s and a lambda that defines the constraints to apply. For example, the following shows a new layout being created to position and size two panels within a container:"]}),"\n",(0,i.jsx)(a.u,{functionName:"createConstraints",height:"300"}),"\n",(0,i.jsx)(s.A,{children:l}),"\n",(0,i.jsx)(t.h2,{id:"readonly-attributes",children:"Readonly attributes"}),"\n",(0,i.jsxs)(t.p,{children:["Sometimes it is necessary to refer to a View's attribute in a constraint without the risk of changing it. This comes up if a View has some property specified outside the constraint block that should be preserved, of if you simply want ",(0,i.jsx)(t.code,{children:"readOnly"})," access to the attribute."]}),"\n",(0,i.jsxs)(t.p,{children:["To do this, you simply use the ",(0,i.jsx)(t.code,{children:"readOnly"})," property of the attribute:"]}),"\n",(0,i.jsx)(s.A,{children:"\n// view1.width won't be modified to satisfy this equation\nview2.width eq view1.width.readOnly * 2\n"}),"\n",(0,i.jsx)(t.h2,{id:"constraint-strength",children:"Constraint strength"}),"\n",(0,i.jsxs)(t.p,{children:["It is possible to define constraints that conflict with each other. Such situations result in an error since they have no clear solution. But you can resolve these conflicts by providing a relative priority or ",(0,i.jsx)(r.Strength,{})," for the constraints in question. This allows the engine to break lower strength constraints when there are conflicts."]}),"\n",(0,i.jsxs)(t.p,{children:["All constraints have the ",(0,i.jsx)(t.code,{children:"Required"})," strength by default. This is the highest possible strength that tells the engine to enforce such a constraint. But you can specify the strength explicitly as follows."]}),"\n",(0,i.jsx)(a.u,{functionName:"constraintStrength",height:"400"}),"\n",(0,i.jsx)(t.p,{children:"This results in the panel matching its parent's width whenever it is 200 or less."}),"\n",(0,i.jsx)(s.A,{children:d}),"\n",(0,i.jsx)(t.admonition,{type:"tip",children:(0,i.jsx)(t.p,{children:"Notice that the constraints indicate a weaker priority/strength for the view's right property. This approach allows you to relax certain constraints when there are conflicts."})}),"\n",(0,i.jsx)(t.p,{children:"The following shows a more complex set of constraints that also use strengths and inequality."}),"\n",(0,i.jsx)(a.u,{functionName:"complexConstraints",height:"400"}),"\n",(0,i.jsx)(s.A,{children:c}),"\n",(0,i.jsx)(t.h2,{id:"parents-are-read-only-by-default",children:"Parents are read-only by default"}),"\n",(0,i.jsxs)(t.p,{children:["Doodle Layouts are all independent and operate primarily on the contents of their container without modifying the container's size. This helps avoid contention where the container itself is within a Layout that disagrees about the size it should have. Constraints support to this approach by having all attributes of the parent container default to read-only. This means the properties of ",(0,i.jsx)(t.code,{children:"parent"})," will not be modified by any constraint as the default. In effect, all parent attributes can be thought of as pure constants."]}),"\n",(0,i.jsx)(s.A,{children:"\n// parent.width can be thought of as constant number\nview.width eq parent.width\n"}),"\n",(0,i.jsxs)(t.p,{children:["However, it can be very useful to propagate size information up to the parent container. A good example is wanting to ensure the parent is large enough to show its children. This can be done by explicitly designating a parent's attribute as ",(0,i.jsx)(t.code,{children:"writable"}),". Doing so means that attribute ",(0,i.jsx)(t.strong,{children:"will"})," be updated as needed to satisfy the constraint."]}),"\n",(0,i.jsx)(s.A,{children:"\n// parent.width will be updated if needed\nview.width eq parent.width.writable\n"}),"\n",(0,i.jsx)(t.h2,{id:"non-siblings-constraints",children:"Non-siblings constraints"}),"\n",(0,i.jsx)(t.p,{children:'You can constrain any set of Views, regardless of their hierarchy. But, the Constraint Layout will only update the Views that within the Container it is laying out. All other Views are treated as readOnly. This adjustment happens automatically as the View hierarchy changes. A key consequence is that Views outside the current parent will not conform to any constraints they "participate" in. This avoids the issue of a layout for one container affecting the children of another.'}),"\n",(0,i.jsx)(s.A,{children:"\nval view1 = view {}\nval view2 = view {}\nval container1 = container {\n    children += view1\n    layout    = constrain(view1, view2) { v1, v2 ->\n        v1.width eq v2.width // v2.width treated as immutable value (i.e. v2.width.readOnly)\n    }\n}\n"}),"\n",(0,i.jsx)(t.h2,{id:"constraints-are-live",children:"Constraints are live"}),"\n",(0,i.jsx)(t.p,{children:'The constraint definitions provided when creating a layout are "live", meaning they are invoked on every layout. This makes it easy to capture external variables or use conditional logic in constraints. But it also means care has to be taken to avoid inefficient layouts. Below is an example of a layout that changes behavior based on a threshold variable.'}),"\n",(0,i.jsx)(s.A,{children:"\nval threshold = 100\n\ncontainer.layout = constrain(view1, view2) { v1, v2 ->\n    // This will result in different constraints being applied dynamically as\n    // container.width crosses the threshold\n    when {\n        container.width < threshold -> it.width eq parent.width / 2\n        else                        -> it.width eq parent.width\n    }\n}\n"}),"\n",(0,i.jsx)(t.h2,{id:"removing-constraints",children:"Removing constraints"}),"\n",(0,i.jsx)(t.p,{children:"Live constraints make it easy to support conditional logic, which covers most cases where constraints may need to be suppressed. But sometimes it is necessary to remove a set of constraints from a layout entirely. This is done in a very similar way to how we define constraints to begin with. Removing constraints is analogous to removing a handler. That is if we think of a constraint block and the list of Views it targets as a single handler that is invoked whenever a layout is triggered. In that way, removing the constraints requires specifying the same list of Views and the block when removing it."}),"\n",(0,i.jsx)(s.A,{children:"\nval constraints: ConstraintDslContext.(Bounds, Bounds) -> Unit = { v1, v2 ->\n  // ...\n}\n\nval layout: ConstraintLayout = constrain(view1, view2, constraints)\n\n// remove constraints applied to these views\nlayout.unconstrain(view1, view2, constraints)\n"}),"\n",(0,i.jsxs)(t.admonition,{type:"caution",children:[(0,i.jsxs)(t.p,{children:["The order and number of Views provided to ",(0,i.jsx)(r.Unconstrain,{})," must match what was used during ",(0,i.jsx)(r.Constrain,{}),". That is because changing order or arity would result in a different constraint effect than was applied."]}),(0,i.jsx)(t.p,{children:"You should store a reference to the constrain block to ensure it is identified as the same one used during register. Do not do the following"}),(0,i.jsx)(s.A,{children:"\nval layout = constrain(view1) { it.center eq parent.center }\n\n// DO NOT DO THIS\nlayout.unconstraint(view1) { it.center eq parent.center }\n"})]})]})}function g(n={}){const{wrapper:t}={...(0,o.M)(),...n.components};return t?(0,i.jsx)(t,{...n,children:(0,i.jsx)(m,{...n})}):m(n)}function v(n,t){throw new Error("Expected "+(t?"component":"object")+" `"+n+"` to be defined: you likely forgot to import, pass, or provide it.")}}}]);
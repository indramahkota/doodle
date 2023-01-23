"use strict";(self.webpackChunkdoodle_docs=self.webpackChunkdoodle_docs||[]).push([[544],{3589:(e,i,t)=>{t.r(i),t.d(i,{assets:()=>r,contentTitle:()=>o,default:()=>p,frontMatter:()=>l,metadata:()=>s,toc:()=>c});var a=t(7462),n=(t(7294),t(3905));t(8209);const l={hide_title:!0},o="Accessibility",s={unversionedId:"accessibility",id:"accessibility",title:"Accessibility",description:"Making truly accessible apps is complex and requires familiarity with a wide range of concepts. The",source:"@site/docs/accessibility.mdx",sourceDirName:".",slug:"/accessibility",permalink:"/doodle/docs/accessibility",draft:!1,tags:[],version:"current",frontMatter:{hide_title:!0},sidebar:"tutorialSidebar",previous:{title:"Themes",permalink:"/doodle/docs/themes"}},r={},c=[{value:"Descriptive Text",id:"descriptive-text",level:2},{value:"<code>accessibilityLabel</code>",id:"accessibilitylabel",level:4},{value:"<code>accessibilityLabelProvider</code>",id:"accessibilitylabelprovider",level:4},{value:"<code>accessibilityDescriptionProvider</code>",id:"accessibilitydescriptionprovider",level:4},{value:"Widget Roles",id:"widget-roles",level:2}],d={toc:c};function p(e){let{components:i,...t}=e;return(0,n.kt)("wrapper",(0,a.Z)({},d,t,{components:i,mdxType:"MDXLayout"}),(0,n.kt)("h1",{id:"accessibility"},"Accessibility"),(0,n.kt)("p",null,"Making truly accessible apps is complex and requires familiarity with a wide range of concepts. The\n",(0,n.kt)("a",{parentName:"p",href:"https://www.w3.org/WAI/intro/wcag"},"Web Content Accessibility Guidelines")," provide recommendations for web apps."),(0,n.kt)("p",null,"Doodle supports accessibility through a number of features. Simply include the\n",(0,n.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Browser/src/jsMain/kotlin/io/nacular/doodle/application/Modules.kt#L104"},(0,n.kt)("inlineCode",{parentName:"a"},"AccessibilityModule")),"\nin your app to fully enable them."),(0,n.kt)("admonition",{type:"info"},(0,n.kt)("p",{parentName:"admonition"},"Only Web apps support this feature currently, as Desktop is still in alpha.")),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},"class MyApp(/*...*/) {\n    // use accessibility features\n}\n\nfun main () {\n    // Include the AccessibilityModule to enable features\n    application(root, modules = listOf(AccessibilityModule)) {\n        MyApp(/*...*/)\n    }\n}\n")),(0,n.kt)("h2",{id:"descriptive-text"},"Descriptive Text"),(0,n.kt)("h4",{id:"accessibilitylabel"},(0,n.kt)("a",{parentName:"h4",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/View.kt#L89"},(0,n.kt)("inlineCode",{parentName:"a"},"accessibilityLabel"))),(0,n.kt)("p",null,"Authors can provide short, descriptive text that is used by assistive technologies to announce a View when it is selected. This property helps\nin cases where a View contains no meaningful text."),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},'val button = PushButton("x").apply {\n    accessibilityLabel = "Close the window"\n}\n')),(0,n.kt)("h4",{id:"accessibilitylabelprovider"},(0,n.kt)("a",{parentName:"h4",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/View.kt#L97"},(0,n.kt)("inlineCode",{parentName:"a"},"accessibilityLabelProvider"))),(0,n.kt)("p",null,"In many cases the app presents descriptive text to the user directly using other Views, like labels for a text fields.\nThe ",(0,n.kt)("inlineCode",{parentName:"p"},"accessibilityLabelProvider"),' points to another View that should be used as a "label" for the current one.'),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},'val label     = Label("Enter your name")\nval textField = TextField().apply {\n    accessibilityLabelProvider = label\n}\n')),(0,n.kt)("admonition",{type:"info"},(0,n.kt)("p",{parentName:"admonition"},"Views can be linked this way at any time, even if they are not both currently displayed. Doodle will track the relationship,\nand surface it to assistive technologies if the Views are simultaneously displayed.")),(0,n.kt)("h4",{id:"accessibilitydescriptionprovider"},(0,n.kt)("a",{parentName:"h4",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/View.kt#L105"},(0,n.kt)("inlineCode",{parentName:"a"},"accessibilityDescriptionProvider"))),(0,n.kt)("p",null,"Labels should be short descriptive names for a View. But it is possible to provide more detailed descriptions as well via the\n",(0,n.kt)("inlineCode",{parentName:"p"},"accessibilityDescriptionProvider"),". This property behaves like ",(0,n.kt)("inlineCode",{parentName:"p"},"accessibilityLabelProvider"),", but is intended for longer, more detailed\ntext that describes the View."),(0,n.kt)("h2",{id:"widget-roles"},"Widget Roles"),(0,n.kt)("p",null,"Authors can indicate that a View plays a well-defined role as a widget by tagging it with an accessibility role. This enables\nassistive technologies to change the presentation of the View to the user as she navigates a scene. This is done by setting the View's\n",(0,n.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/core/View.kt#L80"},(0,n.kt)("inlineCode",{parentName:"a"},"accessibilityRole"))),(0,n.kt)("p",null,"Here is an example of creating a View that will serve as a ",(0,n.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Core/src/commonMain/kotlin/io/nacular/doodle/accessibility/AccessibilityManager.kt#L59"},(0,n.kt)("inlineCode",{parentName:"a"},"ButtonRole")),"."),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},"class CustomButton: View(accessibilityRole = ButtonRole()) {\n    // handle key events, etc.\n}\n")),(0,n.kt)("p",null,"This View will now be treated as a button by accessibility technologies (i.e. screen readers). The ",(0,n.kt)("inlineCode",{parentName:"p"},"button")," role itself does not have\nadditional properties, so simply adopting it is sufficient."),(0,n.kt)("p",null,"Other roles have state and must be synchronized with the View to ensure proper assistive support."),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},"class CustomToggle(private val role: ToggleButtonRole = ToggleButtonRole()): View(accessibilityRole = role) {\n    var selected by observable(false) { old, new ->\n        role.pressed = new\n    }\n}\n")),(0,n.kt)("p",null,"Many of the widgets in the ",(0,n.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/tree/master/Controls"},(0,n.kt)("inlineCode",{parentName:"a"},"Controls"))," library ship with accessibility\nsupport (though this continues to improve). The library also provides bindings for some roles and models, which makes it easier to synchronize\nroles with their widgets. ",(0,n.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Controls/src/commonMain/kotlin/io/nacular/doodle/controls/range/ValueSlider.kt#L13"},(0,n.kt)("inlineCode",{parentName:"a"},"ValueSlider")),",\nfor example, binds its role to the ",(0,n.kt)("a",{parentName:"p",href:"https://github.com/nacular/doodle/blob/master/Controls/src/commonMain/kotlin/io/nacular/doodle/controls/ConfinedRangeModel.kt#L24"},(0,n.kt)("inlineCode",{parentName:"a"},"ConfinedValueModel")),"\nthat underlies it. This way the role and View are always in sync."),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},"public abstract class ValueSlider private constructor(\n                 model: ConfinedValueModel<Double>,\n    protected val role: slider = SliderRole()\n): View(role) {\n    // ...\n\n    // delegate ensures old linkage is broken when new binding established\n    private var roleBinding by binding(role.bind(model))\n\n    public var model: ConfinedValueModel<Double> = model\n        set(new) {\n            // ..\n\n            field = new.also {\n                // ..\n                roleBinding = role.bind(it) // link role to new model\n            }\n        }\n\n    // ...\n}\n")))}p.isMDXComponent=!0}}]);
package com.tmobile.annotations.processor.util;

public class ProcessorConst {
 public static String START_HTML ="<!DOCTYPE html>\r\n" + 
 		"<html>\r\n" + 
 		"<head>\r\n" + 
 		"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\r\n" + 
 		"    <style>\r\n" + 
 		"        .accordion, .accordion:after {\r\n" + 
 		"            font-size: 15px;\r\n" + 
 		"            color: #000\r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .accordion {\r\n" + 
 		"            background-color:#F278A1;\r\n" + 
 		"            cursor: pointer;\r\n" + 
 		"            padding: 10px;\r\n" + 
 		"            width: 300px;\r\n" + 
 		"            border: none;\r\n" + 
 		"            border-radius: 15px;\r\n" + 
 		"            text-align: left;\r\n" + 
 		"            outline: 0;\r\n" + 
 		"            transition: .1s;\r\n" + 
 		"            margin: 10px\r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .active {\r\n" + 
 		"            background-color:#ccc; \r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .accordion:after {\r\n" + 
 		"            content: '\\002B';\r\n" + 
 		"            float: right;\r\n" + 
 		"            margin-left: 5px;\r\n" + 
 		"            padding-right: 10px\r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .active:after {\r\n" + 
 		"            content: \"\\2212\"\r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .panel {\r\n" + 
 		"            padding: 0 18px;\r\n" + 
 		"            background-color: #fff;\r\n" + 
 		"            max-height: 0;\r\n" + 
 		"            overflow: hidden;\r\n" + 
 		"            transition: max-height .1s ease-out\r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .list-type1 {\r\n" + 
 		"            width: 250px;\r\n" + 
 		"            margin: 0 30px\r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .list-type1 ol {\r\n" + 
 		"            counter-reset: li;\r\n" + 
 		"            list-style: none;\r\n" + 
 		"            font-size: 15px;\r\n" + 
 		"            padding: 0;\r\n" + 
 		"            margin-bottom: 4em\r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .list-type1 ol ol {\r\n" + 
 		"            margin: 0 0 0 2em\r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .list-type1 div {\r\n" + 
 		"            position: relative;\r\n" + 
 		"            display: block;\r\n" + 
 		"            padding: .4em .4em .4em 2em;\r\n" + 
 		"            margin: .5em 0;\r\n" + 
 		"            background: #F278A1;\r\n" + 
 		"            color: #000;\r\n" + 
 		"            text-decoration: none;\r\n" + 
 		"            -moz-border-radius: .3em;\r\n" + 
 		"            -webkit-border-radius: .3em;\r\n" + 
 		"            border-radius: 10px;\r\n" + 
 		"            transition: all .2s ease-in-out\r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .list-type1 div:hover {\r\n" + 
 		"            background: #d6d4d4;\r\n" + 
 		"            text-decoration: none;\r\n" + 
 		"            transform: scale(1.1)\r\n" + 
 		"        }\r\n" + 
 		"\r\n" + 
 		"        .list-type1 div:before {\r\n" + 
 		"            content: counter(li);\r\n" + 
 		"            counter-increment: li;\r\n" + 
 		"            position: absolute;\r\n" + 
 		"            left: -1.3em;\r\n" + 
 		"            top: 50%;\r\n" + 
 		"            margin-top: -1.3em;\r\n" + 
 		"            background: #F278A1;\r\n" + 
 		"            height: 2em;\r\n" + 
 		"            width: 2em;\r\n" + 
 		"            line-height: 2em;\r\n" + 
 		"            border: .3em solid #fff;\r\n" + 
 		"            text-align: center;\r\n" + 
 		"            font-weight: 700;\r\n" + 
 		"            -moz-border-radius: 2em;\r\n" + 
 		"            -webkit-border-radius: 2em;\r\n" + 
 		"            border-radius: 2em;\r\n" + 
 		"            color: #FFF\r\n" + 
 		"        }\r\n" + 
 		"    </style>\r\n" + 
 		"</head>\r\n" + 
 		"<body style=\"font-family: 'Raleway', sans-serif;\">\r\n" + 
 		"    <div style=\"font-size: xx-large;text-align: center\">";
 public static String END_HTML ="<script>\r\n" + 
 		"        var acc = document.getElementsByClassName(\"accordion\");\r\n" + 
 		"        var i;\r\n" + 
 		"\r\n" + 
 		"        for (i = 0; i < acc.length; i++) {\r\n" + 
 		"            acc[i].addEventListener(\"click\", function () {\r\n" + 
 		"                this.classList.toggle(\"active\");\r\n" + 
 		"                var panel = this.nextElementSibling;\r\n" + 
 		"                if (panel.style.maxHeight) {\r\n" + 
 		"                    panel.style.maxHeight = null;\r\n" + 
 		"                } else {\r\n" + 
 		"                    panel.style.maxHeight = panel.scrollHeight + \"px\";\r\n" + 
 		"                }\r\n" + 
 		"            });\r\n" + 
 		"        }</script></body></html>";
 
}

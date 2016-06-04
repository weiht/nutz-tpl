var ioc = {
	"core.attachmentModule": {
		type: "tpl.webmods.res.AttachmentModule",
		fields: {
			ioc: {
				refer: "$ioc"
			},
			defaultDao: {
				refer: "coreDao"
			},
			manager: {
				refer: "attachmentManager"
			},
			mimeUtil: {
				refer: "mimeUtil"
			}
		}
	},
	staticServlet: {
		type: "tpl.nutz.web.StaticServlet"
	},
	mimeUtil: {
		type: "tpl.nutz.web.MimeUtil",
		fields: {
			types: {
				".json": "application/json",
				".xml": "application/xml",
				".html": "text/html",
				".htm": "text/html",
				".css": "text/css",
				".js": "text/javascript",
				".doc": "application/msword",
				".dot": "application/msword",
				".docx": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				".dotx": "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
				".docm": "application/vnd.ms-word.document.macroEnabled.12",
				".dotm": "application/vnd.ms-word.template.macroEnabled.12",
				".xls": "application/vnd.ms-excel",
				".xlt": "application/vnd.ms-excel",
				".xla": "application/vnd.ms-excel",
				".xlsx": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				".xltx": "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
				".xlsm": "application/vnd.ms-excel.sheet.macroEnabled.12",
				".xltm": "application/vnd.ms-excel.template.macroEnabled.12",
				".xlam": "application/vnd.ms-excel.addin.macroEnabled.12",
				".xlsb": "application/vnd.ms-excel.sheet.binary.macroEnabled.12",
				".ppt": "application/vnd.ms-powerpoint",
				".pot": "application/vnd.ms-powerpoint",
				".pps": "application/vnd.ms-powerpoint",
				".ppa": "application/vnd.ms-powerpoint",
				".pptx": "application/vnd.openxmlformats-officedocument.presentationml.presentation",
				".potx": "application/vnd.openxmlformats-officedocument.presentationml.template",
				".ppsx": "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
				".ppam": "application/vnd.ms-powerpoint.addin.macroEnabled.12",
				".pptm": "application/vnd.ms-powerpoint.presentation.macroEnabled.12",
				".potm": "application/vnd.ms-powerpoint.template.macroEnabled.12",
				".ppsm": "application/vnd.ms-powerpoint.slideshow.macroEnabled.12"
			}
		}
	}
};
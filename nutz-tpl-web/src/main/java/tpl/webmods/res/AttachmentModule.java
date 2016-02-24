package tpl.webmods.res;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.Dao;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpl.nutz.web.MimeUtil;
import tpl.nutz.web.MultiDaoModule;
import tpl.res.Attachment;
import tpl.res.AttachmentManager;

@InjectName("core.attachmentModule")
@At("/attachments")
public class AttachmentModule
extends MultiDaoModule{
	private static final Logger logger = LoggerFactory.getLogger(AttachmentModule.class);
	private AttachmentManager manager;
	private MimeUtil mimeUtil;

	private Attachment getAttachment(String ds, String id) {
		Dao dao = ensureDao(ds);
		try {
			Attachment a = dao.fetch(Attachment.class, id);
			return a;
		} catch (Exception e) {
			logger.warn("Error loading attachment. ", e);
			return null;
		}
	}
	
	@At("/da/?/?")
	@GET
	@Ok("void") @Fail("http:404")
	public void get(String ds, String id,
			HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		Attachment a = getAttachment(ds, id);
		if (a == null) {
			throw new FileNotFoundException(id);
		}
		File f = manager.findFile(a);
		if (f == null) throw new FileNotFoundException(id);
		String mimeType = a.getMimeType();
		if (Strings.isBlank(mimeType)) {
			mimeType = mimeUtil.getTypeDef(a.getOriginName(), "application/octet-stream");
		}
		resp.setContentType(mimeType);
		String hdr = "attachment;filename=" + java.net.URLEncoder.encode(a.getOriginName(), "UTF-8");
		logger.trace("Content-Disposition: {}", hdr);
		resp.setHeader("Content-Disposition", hdr);
		OutputStream outs = resp.getOutputStream();
		InputStream ins = Streams.fileIn(f);
		try {
			Streams.write(outs, ins);
		} finally {
			Streams.safeClose(ins);
		}
		outs.flush();
	}

	public void setManager(AttachmentManager manager) {
		this.manager = manager;
	}

	public void setMimeUtil(MimeUtil mimeUtil) {
		this.mimeUtil = mimeUtil;
	}
}

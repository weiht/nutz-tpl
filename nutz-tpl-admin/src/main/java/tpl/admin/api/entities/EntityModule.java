package tpl.admin.api.entities;

import org.nutz.dao.Dao;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.PUT;

import tpl.entities.EntityDef;
import tpl.entities.StatusCodes;

@InjectName("api.entityModule")
public class EntityModule {
	private Dao dao;
	
	@At("/entity/?")
	@GET
	public EntityDef get(String name) {
		return dao.fetch(EntityDef.class, name);
	}
	
	@At("/entity")
	@POST
	@AdaptBy(type=JsonAdaptor.class)
	public EntityDef add(EntityDef entity) {
		if (entity == null || entity.getName() == null)
			throw new RuntimeException("No entity definition to save.");
		EntityDef origin = dao.fetch(EntityDef.class, entity.getName());
		if (origin != null)
			throw new RuntimeException("Entity definition already exists.");
		entity.setStatus(StatusCodes.STATUS_BRAND_NEW | StatusCodes.STATUS_UNAPPLIED);
		return dao.insert(entity);
	}
	
	@At("/entity")
	@PUT
	@AdaptBy(type=JsonAdaptor.class)
	public EntityDef update(EntityDef entity) {
		if (entity == null || entity.getName() == null)
			throw new RuntimeException("No entity definition to save.");
		EntityDef origin = dao.fetch(EntityDef.class, entity.getName());
		if (origin == null)
			throw new RuntimeException("Entity definition is not found.");
		origin.setDisplayName(entity.getDisplayName());
		origin.setDescription(entity.getDescription());
		origin.setStatus(origin.getStatus() | StatusCodes.STATUS_UNAPPLIED);
		dao.update(origin);
		return entity;
	}
	
	@At("/entity/?")
	@DELETE
	public EntityDef delete(String name) {
		EntityDef origin = dao.fetch(EntityDef.class, name);
		if (origin != null) {
			dao.delete(origin);
		}
		return origin;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}
}

package tpl.ds;

public interface DataSourceManager {
	public abstract DataSourceDef add(DataSourceDef def);
	public abstract DataSourceDef update(DataSourceDef def);
	public abstract DataSourceDef remove(String name);
	public abstract DataSourceDef activate(String name);
	public abstract DataSourceDef deactivate(String name);
}

package admin_console
import org.apache.shiro.SecurityUtils;

skipView = true;
println SecurityUtils.subject;
println SecurityUtils.subject.principal;

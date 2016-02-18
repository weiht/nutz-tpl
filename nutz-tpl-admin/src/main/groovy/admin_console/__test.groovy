package admin_console
import org.apache.shiro.SecurityUtils
import org.nutz.ioc.Ioc2

import tpl.entities.EntityDef
import tpl.entities.PropertyDef;;;;

skipView = true;
println SecurityUtils.subject;
println SecurityUtils.subject.principal;
Ioc2 ioc2 = ioc;
println ioc2.iocContext.names();
println ioc2.get(null, 'localmysql');
println ioc2.get(null, 'localmysqlDao').exists('sometable')
println ioc2.get(null, 'coreDao').exists(PropertyDef.class)
println ioc2.get(null, 'coreDao').exists(EntityDef.class)

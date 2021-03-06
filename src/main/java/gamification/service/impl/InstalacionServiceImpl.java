package gamification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gamification.exceptions.GrupoExistenteException;
import gamification.exceptions.UsuarioExistenteException;
import gamification.model.Group;
import gamification.model.User;
import gamification.service.GroupService;
import gamification.service.InstalacionService;
import gamification.service.PermissionService;
import gamification.service.UserDetailsService;

@Service
public class InstalacionServiceImpl implements InstalacionService
{
	@Autowired
	private GroupService groupService;
	@Autowired
	private UserDetailsService userService;
	@Autowired
	private PermissionService permissionService;
	
	/**
	 * Graba el usuario administrador. Crea el grupo administrador por defecto,
	 * y hace al usuario que le proporcionan miembro de ese grupo. Este metodo
	 * es parte del proceso de instalacion del sistema y deberia ejecutarse
	 * una sola vez. Es mas, quiza deba verificar en una tabla que esta
	 * instalacion ya se llevo a cabo y no proceder.
	 */
	@Override
	@Transactional
	public void grabarUsuarioAdministrador(User user) throws UsuarioExistenteException,GrupoExistenteException
	{
		// Los metodos del controller de permisos tienen hard-codeado que el
		// ROLE_ADMIN puede entrar, aparte del rol que le 
		// corresponde a ese controller. Esto se hace asi porque tiene
		// que haber un ROLE_ADMIN que tenga siempre acceso a esas pantallas.
		// Otra manera de hacerlo seria aqui crear un usuario creador
		// de permisos, y asignarle los permisos que cada metodo del controller
		// espera. Queda a criterio del implementador usar el ROLE_ADMIN pero
		// es esencial que exista al menos un rol capaz de entrar a la pantalla
		// de permisos. Si no, tenemos el problema del huevo y la gallina,
		// donde tenemos que dar permisos a los usuarios pero todavia no tenemos
		// un usuario porque para crear uno hay que entrar a la pantalla de
		// permisos!!!
		Group adminGroup=new Group();
		adminGroup.setGroup_name("Administradores");
		groupService.save(adminGroup);
		permissionService.grantOrRevokePermission(adminGroup, "ROLE_ADMIN");
		permissionService.grantOrRevokePermission(adminGroup, "ROLE_USER");
		user.setGroup(adminGroup);
		userService.save(user);
	}
}

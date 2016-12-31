package gamification.controller;

import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gamification.documentation.Descripcion;
import gamification.documentation.DescripcionClase;
import gamification.exceptions.CursoExistenteException;
import gamification.model.Curso;
import gamification.model.TemaCurso;
import gamification.service.CursoService;

@Controller
@RequestMapping(value="cursos")
@SessionAttributes("curso")
@DescripcionClase("Cursos")
public class CursosController extends AppController
{
	private static Logger log=LogManager.getLogger(CursosController.class);
	@Autowired
	private CursoService cursoService;
	
	@RequestMapping({"/","/index"})
	@Descripcion(value="Mostrar lista de cursos y menu",permission="ROLE_CURSOS_MOSTRAR_MENU")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_CURSOS_MOSTRAR_MENU')")
	public ModelAndView mostrarMenu()
	{
		ModelAndView modelo=new ModelAndView("cursos_index");
		// Leemos los usuarios que hay.
		modelo.addObject("cursos",cursoService.listarCursos());
		return modelo;
	}
	private ModelAndView cargarFormCurso(String vista,Curso curso)
	{
		ModelAndView modelo=new ModelAndView(vista);
		modelo.addObject("curso",curso);
		return modelo;
	}
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_CURSOS_AGREGAR')")
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public ModelAndView mostrarFormAgregarCurso(Model model)
	{
		ModelAndView modelo=this.cargarFormCurso("cursos_add",new Curso());
		return modelo;
	}
	@Descripcion(value="Agregar Curso",permission="ROLE_CURSOS_AGREGAR")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_CURSOS_AGREGAR')")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ModelAndView agregarCurso(@Valid @ModelAttribute("curso")
	Curso curso,
	BindingResult result,ModelMap model,final RedirectAttributes redirectAttributes)
	{
		if(result.hasErrors())
		{
			List<ObjectError> lista_errores=result.getAllErrors();
			Iterator<ObjectError> i=lista_errores.iterator();
			while(i.hasNext())
			{
				log.trace("Error: "+i.next().toString());
			}
			ModelAndView modelo=this.cargarFormCurso("cursos_add",curso);
			return modelo;
		}
		else
		{
			ModelAndView modelo=new ModelAndView("redirect:/cursos/index");
			try
			{
				cursoService.agregarCurso(curso);
				redirectAttributes.addFlashAttribute("message","Curso agregado exitosamente");
			}
			catch(CursoExistenteException e)
			{
				model.addAttribute("message","Ese nombre de curso ya existe, por favor elija otro");
				modelo=this.cargarFormCurso("cursos_add",curso);
			}
			return modelo;
		}
	}
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_CURSOS_EDIT')")
	@RequestMapping(value="/edit/{cursoId}",method=RequestMethod.GET)
	public ModelAndView mostrarFormEditar(@PathVariable("cursoId") Integer cursoId,
			Model model)
	{
		// Busco el curso y lo cargo en el formulario.
		Curso curso=cursoService.getCursoById(cursoId);
		ModelAndView modelo=this.cargarFormCurso("cursos_edit",curso);
		return modelo;
	}
	@Descripcion(value="Editar Curso",permission="ROLE_CURSOS_EDIT")
	@RequestMapping(value="/edit/{cursoId}",method=RequestMethod.POST)
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_CURSOS_EDIT')")
	public ModelAndView editarCurso(@PathVariable("cursoId") Integer cursoId,
			@Valid @ModelAttribute("curso") Curso curso,
			BindingResult result,ModelMap model,final RedirectAttributes redirectAttributes)
	{
		if(result.hasErrors())
		{
			List<ObjectError> lista_errores=result.getAllErrors();
			Iterator<ObjectError> i=lista_errores.iterator();
			while(i.hasNext())
			{
				log.trace("Error: "+i.next().toString());
			}
			ModelAndView modelo=this.cargarFormCurso("cursos_edit",curso);
			return modelo;
		}
		else
		{
			ModelAndView modelo=new ModelAndView("redirect:/cursos/index");
			try
			{
				cursoService.grabarCurso(curso);
				redirectAttributes.addFlashAttribute("message","Curso editado exitosamente");
			}
			catch(CursoExistenteException e)
			{
				model.addAttribute("message","Ese nombre de curso ya existe, por favor elija otro");
				modelo=this.cargarFormCurso("cursos_edit",curso);
			}
			return modelo;
		}
	}
	@Descripcion(value="Listar temario del curso",permission="ROLE_TEMARIO_LISTAR")
	@RequestMapping(value="/temario/{cursoId}",method=RequestMethod.GET)
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_TEMARIO_LISTAR')")
	public ModelAndView listarTemario(@PathVariable("cursoId") Integer cursoId)
	{
		ModelAndView m=new ModelAndView("cursos_temario_index");
		Curso curso=cursoService.getCursoById(cursoId);
		List<TemaCurso> temas=cursoService.listarTemas(cursoId);
		m.addObject("curso",curso);
		m.addObject("temario",temas);
		return m;
	}
	private ModelAndView cargarFormTema(String vista,TemaCurso tema)
	{
		ModelAndView modelo=new ModelAndView(vista);
		modelo.addObject("tema",tema);
		return modelo;
	}
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_CURSOS_TEMA_AGREGAR')")
	@RequestMapping(value="/temario_add/{cursoId}",method=RequestMethod.GET)
	public ModelAndView mostrarFormAgregarTema(Model model,
			@PathVariable("cursoId") Integer cursoId
			)
	{
		TemaCurso t=new TemaCurso();
		ModelAndView modelo=this.cargarFormTema("cursos_temario_add",t);
		return modelo;
	}
	@Descripcion(value="Agregar Tema de Curso",permission="ROLE_CURSOS_TEMA_AGREGAR")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_CURSOS_TEMA_AGREGAR')")
	@RequestMapping(value = "/temario_add/{cursoId}", method = RequestMethod.POST)
	public ModelAndView agregarTemaCurso(@Valid @ModelAttribute("tema")
	@PathVariable("cursoId") Integer cursoId,
	TemaCurso tema,
	BindingResult result,ModelMap model,final RedirectAttributes redirectAttributes)
	{
		if(result.hasErrors())
		{
			List<ObjectError> lista_errores=result.getAllErrors();
			Iterator<ObjectError> i=lista_errores.iterator();
			while(i.hasNext())
			{
				log.trace("Error: "+i.next().toString());
			}
			ModelAndView modelo=this.cargarFormTema("cursos_temario_add",tema);
			return modelo;
		}
		else
		{
			ModelAndView modelo=new ModelAndView("redirect:/cursos/temario/"+cursoId);
			cursoService.agregarTemaCurso(tema,cursoId);
			redirectAttributes.addFlashAttribute("message","Curso agregado exitosamente");
			return modelo;
		}
	}
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_CURSOS_TEMA_EDIT')")
	@RequestMapping(value="/temario_edit/{temaId}",method=RequestMethod.GET)
	public ModelAndView mostrarFormEditarTema(@PathVariable("temaId") Integer temaId,
			Model model)
	{
		// Busco el curso y lo cargo en el formulario.
		TemaCurso tema=cursoService.getTemaById(temaId);
		ModelAndView modelo=this.cargarFormTema("cursos_temario_edit",tema);
		return modelo;
	}
	@Descripcion(value="Editar Tema de curso",permission="ROLE_CURSOS_TEMA_EDIT")
	@RequestMapping(value="/temario_edit/{temaId}",method=RequestMethod.POST)
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_CURSOS_TEMA_EDIT')")
	public ModelAndView editarTema(@PathVariable("temaId") Integer temaId,
			@Valid @ModelAttribute("tema") TemaCurso tema,
			BindingResult result,ModelMap model,final RedirectAttributes redirectAttributes)
	{
		if(result.hasErrors())
		{
			List<ObjectError> lista_errores=result.getAllErrors();
			Iterator<ObjectError> i=lista_errores.iterator();
			while(i.hasNext())
			{
				log.trace("Error: "+i.next().toString());
			}
			ModelAndView modelo=this.cargarFormTema("cursos_temario_edit",tema);
			return modelo;
		}
		else
		{
			ModelAndView modelo=new ModelAndView("redirect:/cursos/temario/"+tema.getCurso().getId());
			cursoService.grabarTemaCurso(tema);
			redirectAttributes.addFlashAttribute("message","Tema editado exitosamente");
			return modelo;
		}
	}
}

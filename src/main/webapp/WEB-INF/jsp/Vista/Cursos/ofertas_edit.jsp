<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<script type="text/javascript">
<%@include file="/WEB-INF/jsp/Vista/Cursos/ofertas_cursos.js" %>
</script>

<form:form method="post" commandName="oferta">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<form:input id="OfertaId" class="form-control" path="id"/>
	<tiles:insertAttribute name="oferta_form"/>
	<input type="submit" name="editar_oferta" value="Editar Oferta de Curso">
</form:form>
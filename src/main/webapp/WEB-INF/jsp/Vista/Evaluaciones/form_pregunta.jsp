<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<form:label path="tipo_pregunta" id="LabelTipoPregunta">Tipo Pregunta</form:label>
<form:select path="tipo_pregunta" id="PreguntaTipoPregunta" class="form-control">
	<c:forEach items="${tipos_preguntas}" var="tipo">
		<option value="<c:out value="${tipo.id}"/>"><c:out value="${tipo.descripcion}"/></option>
	</c:forEach>
</form:select>
<form:label path="texto_pregunta" id="LabelPreguntaTextoPregunta">Texto de la pregunta</form:label>
<form:input id="PreguntaTextoPregunta" class="form-control" path="texto_pregunta"/>
<form:label path="texto_ordenar" id="LabelPreguntaTextoOrdenar">Informaci&oacute;n adicional de la pregunta</form:label>
<form:textarea cols="80" rows="10" id="PreguntaTextoOrdenar" class="form-control" path="texto_ordenar"/>
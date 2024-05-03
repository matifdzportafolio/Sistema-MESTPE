package ar.edu.utn.frba.dds.models.entidades.serviciospublicos;

import ar.edu.utn.frba.dds.models.entidades.Persistente;
import ar.edu.utn.frba.dds.models.entidades.serviciosapi.Localizacion;
import ar.edu.utn.frba.dds.models.entidades.serviciosapi.LocalizacionAsignada;
import ar.edu.utn.frba.dds.models.entidades.incidentes.Incidente;
import ar.edu.utn.frba.dds.models.entidades.operadorFechas.operadorFechas;
import ar.edu.utn.frba.dds.models.repositorios.RepositorioEntidades;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.print.attribute.standard.MediaSize;
import java.io.IOException;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "entidad")

public class Entidad extends Persistente {
  @Column(name = "nombre")
  private String nombre;

  @Embedded
  @Column(name = "localizacion")
  private Localizacion localizacion;


  //@OneToMany(cascade = CascadeType.ALL)
  @OneToMany()
  @JoinColumn(name = "entidad_id", referencedColumnName = "id")
  private List<Establecimiento> establecimientos;

  @Enumerated
  private TipoEntidad tipo_entidad;


  //@OneToMany(cascade = CascadeType.ALL)
  @OneToMany
  @JoinColumn(name = "entidad_id", referencedColumnName = "id")
  private List<Incidente> incidentes;


  public Entidad(String nombre, List<Establecimiento> estaciones, TipoEntidad tipoEntidad) {
    this.nombre = nombre;
    this.establecimientos = Collections.unmodifiableList(estaciones);
    this.tipo_entidad = tipoEntidad;
  }

  public Entidad() {

  }

  public String getNombre() {
    return nombre;
  }

  public List<Establecimiento> getEstablecimientos() {
    return Collections.unmodifiableList(establecimientos);
  }

  /*
  public void setLocalizacion(String localizacion) throws IOException {
    this.localizacionAsignada.buscarLocalizacion(localizacion);
  }*/


  public void agregarIncidente(Incidente incidente) {
    incidentes.add(incidente);
  }


  public String promedioCierreIncidentes(){
    List<Incidente> incidentesCerrados =  this.incidentesDeLaSemana().stream().filter(i->!i.getEstaAbierto()).collect(Collectors.toList());
    operadorFechas calculador =new operadorFechas();
    return calculador.promedioDiasTotales(incidentesCerrados)+":"+calculador.promedioHorasTotales(incidentesCerrados)+":"+calculador.promedioMinutosTotales(incidentesCerrados);
  }


  public double promedioCierreIncidentesEnMinutos(){
    List<Incidente> incidentesCerrados =  this.incidentesDeLaSemana().stream().filter(i->!i.getEstaAbierto()).collect(Collectors.toList());
    operadorFechas calculador = new operadorFechas();
    double resultado = ((calculador.promedioDiasTotales(incidentesCerrados)*24*60) + (calculador.promedioHorasTotales(incidentesCerrados)*60)+calculador.promedioMinutosTotales(incidentesCerrados));
    System.out.println(resultado + " " + "RESULTADO DEL PROMEDIOOOOOOOOO");
    return resultado;
  }


  public int cantidadIncidentesEnLaSemana(){
    return  this.incidentesDeLaSemana().size();
  }


  public int gradoDeProblematica(){
    return  this.incidentesDeLaSemana().stream().mapToInt(Incidente::cantidadAfectados).sum();
  }
  public List<Incidente> incidentesDeLaSemana(){
    System.out.println(RepositorioEntidades.getInstance().getEntidades().get(0).getIncidentes().get(0).getFechaApertura() +" " + "FECHA DE APERTURA DEL PRIMER INCIDENTE DE LA PRIMERA ENTIDAD");
    Calendar calendar = Calendar.getInstance();
    //int semanaDelAnioActual = calendar.get(Calendar.WEEK_OF_YEAR) - 1;   PARA CORRER LOCAL X SISTEMA OPERATIVO
    int semanaDelAnioActual = calendar.get(Calendar.WEEK_OF_YEAR);         //PARA CORRER EN RENDER
    System.out.println(semanaDelAnioActual + "SEMANA DEL AÑO ACTUAL SEGUN CALENDAR");
    System.out.println(RepositorioEntidades.getInstance().getEntidades().get(0).getIncidentes().get(0).getFechaApertura().get(WeekFields.ISO.weekOfYear()) + "SEMANA DEL INCIDNETE SEGUN WEEKFIELDS" );
    List<Incidente> incidentesFiltrados = this.incidentes.stream().filter(i->i.getFechaApertura().get(WeekFields.ISO.weekOfYear())==semanaDelAnioActual).collect(Collectors.toList());
    return  incidentesFiltrados;
  }

  public Localizacion getLocalizacionAsignada() {
    return localizacion;
  }

  public void asingarLocalizacion(String localizacion,LocalizacionAsignada buscador) throws IOException {
    Localizacion localizacionEncontrada = buscador.buscarLocalizacion(localizacion);
    this.localizacion = localizacionEncontrada;
    }



  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  /*
  public void setLocalizacionAsignada(LocalizacionAsignada localizacionAsignada) {
    this.localizacionAsignada = localizacionAsignada;
  }*/

  public void setEstablecimientos(List<Establecimiento> establecimientos) {
    this.establecimientos = establecimientos;
  }

  public void setTipoEntidad(TipoEntidad tipoEntidad) {
    this.tipo_entidad = tipoEntidad;
  }

  public void setIncidentes(List<Incidente> incidentes) {
    this.incidentes = incidentes;
  }

  public void agregarEstablecimiento(Establecimiento establecimiento){
    this.establecimientos.add(establecimiento);
    establecimiento.setEntidad(this);
  }


  public Localizacion getLocalizacion() {
    return localizacion;
  }

  public TipoEntidad getTipo_entidad() {
    return tipo_entidad;
  }

  public List<Incidente> getIncidentes() {
    return incidentes;
  }
}

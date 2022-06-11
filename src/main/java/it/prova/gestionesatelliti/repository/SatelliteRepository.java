package it.prova.gestionesatelliti.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import it.prova.gestionesatelliti.model.Satellite;
import it.prova.gestionesatelliti.model.StatoSatellite;

public interface SatelliteRepository extends CrudRepository<Satellite, Long>, JpaSpecificationExecutor<Satellite> {

	// Lanciati da due anni con stato non disattivato
	List<Satellite> findByDataLancioBeforeAndStatoNot(Date data, StatoSatellite statoDisattivato);

	// disattivati ma con data rientro a null
	List<Satellite> findByDataRientroNullAndStatoIs(StatoSatellite statoDisattivato);

	// Rimasti in orbita 10 anni ma che ora sono fissi
	List<Satellite> findByDataLancioBeforeAndStato(Date data, StatoSatellite statoFisso);
}
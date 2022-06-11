package it.prova.gestionesatelliti.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.prova.gestionesatelliti.model.Satellite;
import it.prova.gestionesatelliti.model.StatoSatellite;

@Component
public class SatelliteValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {

		return Satellite.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {

		Satellite satellite = (Satellite) obj;

		// Se lo stato è fisso o in movimento la data rientro deve essere == a null
		if ((satellite.getStato() == StatoSatellite.IN_MOVIMENTO || satellite.getStato() == StatoSatellite.FISSO)
				&& satellite.getDataRientro() != null) {
			errors.rejectValue("stato", "dataRientro.deveEssereNullSeStatoFissoOInMovimento");
		}

		// Se lo stato è disattivato la data rientro deve essere != da null
		if (satellite.getStato() == StatoSatellite.DISATTIVATO && satellite.getDataRientro() == null) {
			errors.rejectValue("stato", "dataRientro.nonPuoEssereNullSeStatoDisattivato");
		}

		// Se si imposta una Data di Rientro bisogna impostare anche una Data di Lancio
		if (satellite.getDataLancio() == null && satellite.getDataRientro() != null) {
			errors.rejectValue("dataRientro", "dataRientro.conDataLancio");
		}

		// La data di rientro deve essere più recente a quella di lancio
		if (satellite.getDataLancio() != null && satellite.getDataRientro() != null
				&& satellite.getDataRientro().before(satellite.getDataLancio())) {
			errors.rejectValue("dataLancio", "dataRientro.primaDataLancio");
		}

	}

}

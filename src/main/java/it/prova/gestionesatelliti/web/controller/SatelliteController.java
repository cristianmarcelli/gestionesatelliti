package it.prova.gestionesatelliti.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.prova.gestionesatelliti.model.Satellite;
import it.prova.gestionesatelliti.model.StatoSatellite;
import it.prova.gestionesatelliti.service.SatelliteService;
import it.prova.gestionesatelliti.validator.SatelliteValidator;

@Controller
@RequestMapping(value = "/satellite")
public class SatelliteController {

	@Autowired
	private SatelliteService satelliteService;
	@Autowired
	private SatelliteValidator satelliteValidator;

	@GetMapping("/listAll")
	public ModelAndView listAll() {
		ModelAndView mv = new ModelAndView();
		List<Satellite> results = satelliteService.listAllElements();
		mv.addObject("satellite_list_attribute", results);
		mv.setViewName("satellite/list");
		return mv;
	}

	@GetMapping("/insert")
	public String create(Model model) {
		model.addAttribute("insert_satellite_attr", new Satellite());
		return "satellite/insert";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("insert_satellite_attr") Satellite satellite, BindingResult result,
			RedirectAttributes redirectAttrs) {

		satelliteValidator.validate(satellite, result);

		if (result.hasErrors())
			return "satellite/insert";

		satelliteService.inserisciNuovo(satellite);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite/listAll";
	}

	@GetMapping("/show/{idSatellite}")
	public String show(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("show_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/show";
	}

	@GetMapping("/delete/{idSatellite}")
	public String delete(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("delete_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));

		return "satellite/delete";
	}

	@PostMapping("/remove")
	public String remove(@RequestParam(required = true) Long idSatellite, RedirectAttributes redirectAttrs) {

		if (satelliteValidator.validateDelete(satelliteService.caricaSingoloElemento(idSatellite))) {
			redirectAttrs.addFlashAttribute("failedMessage", "Impossibile eliminare il satellite");
			return "redirect:/satellite/listAll";
		}

		satelliteService.rimuovi(idSatellite);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite/listAll";
	}

	@GetMapping("/edit/{idSatellite}")
	public String edit(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("update_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/edit";
	}

	@PostMapping("/update")
	public String update(@Valid @ModelAttribute("update_satellite_attr") Satellite satellite, BindingResult result,
			RedirectAttributes redirectAttributes) {

		satelliteValidator.validate(satellite, result);

		if (result.hasErrors())
			return "satellite/edit";

		satelliteService.aggiorna(satellite);

		redirectAttributes.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite/listAll";
	}

	@GetMapping("/search")
	public String search() {
		return "satellite/search";
	}

	@PostMapping("/list")
	public String listByExample(Satellite example, ModelMap model) {
		List<Satellite> results = satelliteService.findByExample(example);
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}

	@GetMapping("/lancia/{idSatellite}")
	public String lancia(@PathVariable(required = true) Long idSatellite, Model model,
			RedirectAttributes redirectAttributes) {

		Satellite satellite = satelliteService.caricaSingoloElemento(idSatellite);
		satellite.setStato(StatoSatellite.IN_MOVIMENTO);
		satellite.setDataLancio(new Date());

		satelliteService.aggiorna(satellite);

		redirectAttributes.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite/listAll";

	}

	@GetMapping("/rientra/{idSatellite}")
	public String rientra(@PathVariable(required = true) Long idSatellite, Model model,
			RedirectAttributes redirectAttributes) {

		Satellite satellite = satelliteService.caricaSingoloElemento(idSatellite);
		satellite.setStato(StatoSatellite.DISATTIVATO);
		satellite.setDataRientro(new Date());

		satelliteService.aggiorna(satellite);

		redirectAttributes.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite/listAll";
	}

	@GetMapping("/lanciati")
	public String lanciatiDaPiuDiDueAnniENonDisattivati(ModelMap model) throws ParseException {

		List<Satellite> results = satelliteService.trovaSatellitiLanciatiDaPiuDiDueAnniENonDisattivati(
				new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020"), StatoSatellite.DISATTIVATO);

		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";

	}

	@GetMapping("/disattivati")
	public String disattivatiConDataRientroNull(ModelMap model) throws ParseException {

		List<Satellite> results = satelliteService
				.trovaSatellitiDisattivatiConDataRientroNull(StatoSatellite.DISATTIVATO);

		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";

	}
	
	@GetMapping("/fissi")
	public String fissiInOrbitaDaDieciAnni(ModelMap model) throws ParseException {

		List<Satellite> results = satelliteService.trovaSatellitiInOrbitaDaDieciAnniConStatoFisso(
				new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2012"), StatoSatellite.FISSO);

		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";

	}

}
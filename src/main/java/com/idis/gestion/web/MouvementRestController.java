package com.idis.gestion.web;

import com.idis.gestion.entities.Employe;
import com.idis.gestion.entities.Facture;
import com.idis.gestion.entities.Mouvement;
import com.idis.gestion.entities.Utilisateur;
import com.idis.gestion.service.MouvementService;
import com.idis.gestion.service.PersonneService;
import com.idis.gestion.service.UtilisateurService;
import com.idis.gestion.service.pagination.PageFacture;
import com.idis.gestion.service.pagination.PageMouvement;
import com.idis.gestion.web.controls.HeadersControls;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MouvementRestController {

    @Autowired
    private MouvementService mouvementService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private PersonneService personneService;

    private HeadersControls headersControls = new HeadersControls();

    @GetMapping(value = "/user/search-factures")
    public PageFacture searchFactures(
            @RequestHeader(value = "Authorization") String jwt,
            @RequestParam(name = "numeroFacture", defaultValue = "") String numeroFacture,
            @RequestParam(name = "referenceColis", defaultValue = "") String referenceColis,
            @RequestParam(name = "nomTypeFacture", defaultValue = "") String nomTypeFacture,
            @RequestParam(name = "enable", defaultValue = "1") int enable,
            Pageable pageable
    ){
        int actif = headersControls.getIsAdmin(jwt, enable);
        String username = headersControls.getUsername(jwt);
        Utilisateur utilisateur = utilisateurService.findUserByUsername(username);
        if(utilisateur.getPersonne() == null) throw new RuntimeException("Vous n'êtes pas autorisé");
        Employe employe = personneService.getEmployeById(utilisateur.getPersonne().getId());
        return mouvementService.listFacture(numeroFacture, referenceColis, nomTypeFacture, employe.getSite().getCodeSite(), actif, pageable);
    }

    @GetMapping(value = "/user/all-factures")
    public List<Facture> allFactures(
            @RequestHeader(value = "Authorization") String jwt
    ){
        int actif = headersControls.getIsAdmin(jwt, 1);
        String username = headersControls.getUsername(jwt);
        Utilisateur utilisateur = utilisateurService.findUserByUsername(username);
        Employe employe = personneService.getEmployeById(utilisateur.getPersonne().getId());
        return mouvementService.findAllFactures(employe, actif);
    }

    @GetMapping(value = "/user/factures-by-numeroFacture")
    public List<Facture> facturesByNumeroFacture(
            @RequestHeader(value = "Authorization") String jwt,
            @RequestParam(name = "numeroFacture", defaultValue = "") String numeroFacture
    ){
        int actif = headersControls.getIsAdmin(jwt, 1);
        String username = headersControls.getUsername(jwt);
        Utilisateur utilisateur = utilisateurService.findUserByUsername(username);
        Employe employe = personneService.getEmployeById(utilisateur.getPersonne().getId());
        return mouvementService.findFacturesByNumeroFacture(numeroFacture, employe, actif);
    }

    @GetMapping(value = "/user/take-facture")
    public Facture getFacture(
            @RequestParam(name = "numeroFacture", defaultValue = "") String numeroFacture
    ){
        return mouvementService.getFactureByNumeroFacture(numeroFacture);
    }

    @GetMapping(value = "/user/facture-pdf")
    public @ResponseBody void getFacturePDF(
            @RequestParam(name = "numeroFacture", defaultValue = "") String numeroFacture,
            HttpServletResponse response
    ){

        try {

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"facture.pdf\"");

            final OutputStream out = response.getOutputStream();
            JasperPrint print = mouvementService.exportFacturePdf(numeroFacture);
            JasperExportManager.exportReportToPdfStream(print, out);

        } catch (SQLException | IOException | JRException e) {
            e.printStackTrace();
        }

    }

    @PostMapping(value = "/user/add-facture")
    public Facture addFacture(
            @RequestHeader(value = "Authorization") String jwt,
            @RequestBody Facture facture
    ){
        String username = headersControls.getUsername(jwt);
        Utilisateur utilisateur = utilisateurService.findUserByUsername(username);
        if(utilisateur.getPersonne() == null) throw new RuntimeException("Vous n'êtes pas autorisé");
        /*if(facture.getDateEcheance() != null){
            Date newDate = new Date();
            if(facture.getDateEcheance().compareTo(newDate) < 0) throw new RuntimeException("La date d'échéance ne peut inférieure à la date actuelle");
        }*/
        Employe employe = personneService.getEmployeById(utilisateur.getPersonne().getId());
        facture.setUtilisateur(utilisateur);
        facture.setDevise(employe.getSite().getDevise());
        facture.setTva(employe.getSite().getTva());
        facture.setSite(employe.getSite());

        return mouvementService.saveFacture(facture);
    }

    @PostMapping(value = "/user/update-facture")
    public Facture updateFacture(
            @RequestBody Facture facture
    ){
        return mouvementService.updateFacture(facture);
    }
    @PostMapping("/user/disable-mouvement")
    public boolean disableMouvement(@RequestBody Mouvement mouvement){
        mouvementService.disableFacture(mouvement.getId(), new Date());
        return true;
    }

    @PostMapping("/admin/enable-mouvement")
    public boolean enableMouvement(@RequestBody Mouvement mouvement){
        mouvementService.enableFacture(mouvement.getId(), new Date());
        return true;
    }

    @PostMapping("/admin/remove-mouvement")
    public boolean removeMouvement(@RequestBody Mouvement mouvement){
        mouvementService.removeFactureById(mouvement.getId());
        return true;
    }
}

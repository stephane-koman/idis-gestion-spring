package com.idis.gestion.service;

import com.idis.gestion.entities.Colis;
import com.idis.gestion.entities.Utilisateur;
import com.idis.gestion.service.pagination.PageColis;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface ColisService {

    public Colis saveColis(Colis c, String codeSite);

    public Colis findColisByReference(String ref);

    public Colis updateColis(Colis c);

    public PageColis listColis(
            String reference,
            String nomClient,
            String nomDestinataire,
            int enable,
            Pageable pageable
    );

    public PageColis sendListColis(
            String reference,
            String nomClient,
            String nomDestinataire,
            String nomSite,
            int enable,
            Pageable pageable
    );

    public List<Colis> findAllSendColis(String nomSite, int enable);

    public PageColis receiveListColis(
            String reference,
            String nomClient,
            String nomDestinataire,
            String nomSite,
            int enable,
            Pageable pageable
    );

    public PageColis clientListColis(
            String reference,
            Long idClient,
            String nomDestinataire,
            int enable,
            Pageable pageable
    );

    public void disableColis(String ref, Date date);
    public void enableColis(String ref, Date date);

    public void removeColisByReference(String ref);

    //------------------ OTHER ---------------------------------
    public Colis addEnregistrementColis(Colis colis);
    public Colis updateEnregistrementColis(Colis colis);
    public Colis addExpeditionColis(Colis colis, Utilisateur utilisateur);
    public Colis updateExpeditionColis(Colis colis);
    public Colis addArriveeColis(Colis colis, Utilisateur utilisateur);
    public Colis updateArriveeColis(Colis colis);
    public Colis addReceptionColis(Colis colis, Utilisateur utilisateur);
    public Colis updateReceptionColis(Colis colis);
    public Colis addLivraisonColis(Colis colis, Utilisateur utilisateur);
    public Colis updateLivraisonColis(Colis colis);

    // ----------------------- DASHBORD ---------------------------------------------------
    public String countSendColis(Long idSite, int enable);
    public String countReceiveColis(Long idSite, int enable);
    public String countClientColis(Long idClient, int enable);

}

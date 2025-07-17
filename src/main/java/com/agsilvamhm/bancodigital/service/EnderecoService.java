package com.agsilvamhm.bancodigital.service;

import com.agsilvamhm.bancodigital.controller.exception.EnderecoServiceException;
import com.agsilvamhm.bancodigital.controller.exception.EntidadeNaoEncontradaException;
import com.agsilvamhm.bancodigital.model.Endereco;
import com.agsilvamhm.bancodigital.repository.EnderecoDao;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class EnderecoService {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoService.class);
    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    private final EnderecoDao enderecoDao;
    private final RestTemplate restTemplate;

    @Autowired
    public EnderecoService(EnderecoDao enderecoDao, RestTemplate restTemplate) {
        this.enderecoDao = enderecoDao;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public Endereco buscarOuSalvarEnderecoPorCep(String cep) {
        // Remove caracteres não numéricos do CEP
        String cepLimpo = cep.replaceAll("[^0-9]", "");

        // 1. Tenta buscar no banco de dados
        Optional<Endereco> enderecoExistente = enderecoDao.buscarPorCep(cepLimpo);

        if (enderecoExistente.isPresent()) {
            logger.info("Endereço encontrado no banco de dados para o CEP: {}", cepLimpo);
            return enderecoExistente.get();
        } else {
            logger.info("Endereço não encontrado no banco de dados. Consultando ViaCEP para o CEP: {}", cepLimpo);
            // 2. Se não encontrar, consulta a API pública (ViaCEP)
            try {
                Endereco enderecoViaCep = restTemplate.getForObject(VIACEP_URL, Endereco.class, cepLimpo);

                if (enderecoViaCep != null && enderecoViaCep.getCep() != null) {
                    // ViaCEP retorna o CEP formatado, mas o campo 'cep' no JSON também pode vir como "cep": "01001-000"
                    // Precisamos garantir que o CEP salvo no BD esteja limpo.
                    enderecoViaCep.setCep(cepLimpo); // Garante que o CEP salvo é o limpo

                    // 3. Salva no banco de dados
                    Integer idGerado = enderecoDao.salvar(enderecoViaCep);
                    enderecoViaCep.setId(idGerado);
                    logger.info("Endereço salvo no banco de dados após consulta ViaCEP para o CEP: {}", cepLimpo);
                    return enderecoViaCep;
                } else {
                    logger.warn("ViaCEP não retornou dados para o CEP: {}", cepLimpo);
                    throw new EntidadeNaoEncontradaException("CEP " + cep + " não encontrado na API externa.");
                }
            } catch (HttpClientErrorException.BadRequest ex) {
                logger.error("Erro de requisição ao ViaCEP para o CEP {}: {}", cepLimpo, ex.getMessage());
                throw new EnderecoServiceException("CEP " + cep + " inválido ou não encontrado.", ex);
            } catch (Exception ex) {
                logger.error("Erro ao consultar ViaCEP ou salvar endereço para o CEP {}: {}", cepLimpo, ex.getMessage());
                throw new EnderecoServiceException("Erro ao buscar ou salvar endereço para o CEP " + cep + ".", ex);
            }
        }
    }
}
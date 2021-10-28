package com.template.webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.template.IOUState;
import com.template.createProductFlow;
import com.template.model.Response;
import com.template.updateProductStatusFlow;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Define your API endpoints here.
 */

@RestController
//@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
   // private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @Bean //to be commented
    public static MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(mapper);
        return converter;
    }

    private static List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> converters =
                new ArrayList<HttpMessageConverter<?>>();
        converters.add(mappingJackson2HttpMessageConverter());
        return converters;
    }

    @CrossOrigin
    @RequestMapping(value = "/products", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> createProduct(@RequestBody ProductInfo productInfo) {
        try {
            CordaX500Name partyX500Name = CordaX500Name.parse(productInfo.getOtherParty());
            Party partyName = proxy.wellKnownPartyFromX500Name(partyX500Name);
            CordaFuture<SignedTransaction> signedTx = proxy.startFlowDynamic(createProductFlow.class,
                    productInfo.getProductName(),
                    productInfo.getProductColor(),
                    productInfo.getStatus(),
                    partyName).getReturnValue();
            return new ResponseEntity<>(new Response("Product Created Successfully")
                    , HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(e.getMessage()));
        }
    }
   /* @GetMapping(value = "/status",produces = "application/json")
            public ResponseEntity<List<StateAndRef<IOUState>>> getState(){

        }*/

    @RequestMapping(value = "/getproduct", method = RequestMethod.GET)
    public ResponseEntity<String> getProduct() {
        try {
            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
            Vault.Page<ContractState> results = proxy.vaultQueryByCriteria(generalCriteria,IOUState.class);
            return new ResponseEntity(results,HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @CrossOrigin
    @RequestMapping(value = "/receivedproducts", method = RequestMethod.POST)
    public ResponseEntity<Response> receivedProduct(@RequestBody ProductStatus status) {
        try {

            CordaFuture<SignedTransaction> signedTx = proxy.startFlowDynamic(updateProductStatusFlow.class,
                    status.getStatus(),
                    status.getLinearId())
                    .getReturnValue();
            return new ResponseEntity<>(new Response("Product status changed to received successfully ")
                    , HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(e.getMessage()));
        }


    }
}

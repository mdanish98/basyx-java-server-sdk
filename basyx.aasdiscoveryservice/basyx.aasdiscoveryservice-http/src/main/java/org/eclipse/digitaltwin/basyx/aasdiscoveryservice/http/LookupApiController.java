package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetID;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http.pagination.InlineResponse200;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResultPagingMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-10-10T10:16:17.046754509Z[GMT]")
@RestController
public class LookupApiController implements LookupApi {

    private final AasDiscoveryService aasDiscoveryService;

    @Autowired
    public LookupApiController(AasDiscoveryService aasDiscoveryService) {
    	this.aasDiscoveryService = aasDiscoveryService;
    }

    public ResponseEntity<Void> deleteAllAssetLinksById(@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier) {
    	aasDiscoveryService.deleteAllAssetLinksById(aasIdentifier.getIdentifier());
    	
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<PagedResult> getAllAssetAdministrationShellIdsByAssetLink(@Parameter(in = ParameterIn.QUERY, description = "A list of specific Asset identifiers. Each Asset identifier is a base64-url-encoded [SpecificAssetId](https://api.swaggerhub.com/domains/Plattform_i40/Part1-MetaModel-Schemas/V3.0.1#/components/schemas/SpecificAssetId)" ,schema=@Schema()) @Valid @RequestParam(value = "assetIds", required = false) List<Base64UrlEncodedIdentifier> assetIds,@Min(1)@Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array" ,schema=@Schema(allowableValues={ "1" }, minimum="1"
)) @Valid @RequestParam(value = "limit", required = false) Integer limit,@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue" ,schema=@Schema()) @Valid @RequestParam(value = "cursor", required = false) String cursor) {
        
    	if (limit == null)
			limit = 100;
    	
		if (cursor == null)
			cursor = "";

		PaginationInfo pInfo = new PaginationInfo(limit, cursor);
		
		List<String> decodedAssetIds = assetIds.stream().map(assetId -> assetId.getIdentifier()).collect(Collectors.toList());

		CursorResult<List<String>> filteredResult = aasDiscoveryService.getAllAssetAdministrationShellIdsByAssetLink(pInfo, decodedAssetIds);

		InlineResponse200 paginatedAasIds = new InlineResponse200();
		paginatedAasIds.setResult(filteredResult.getResult());
		paginatedAasIds.setPagingMetadata(new PagedResultPagingMetadata().cursor(filteredResult.getCursor()));

		return new ResponseEntity<PagedResult>(paginatedAasIds, HttpStatus.OK);
    }

    public ResponseEntity<List<SpecificAssetID>> getAllAssetLinksById(@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier) {
        List<SpecificAssetID> assetLinks = aasDiscoveryService.getAllAssetLinksById(aasIdentifier.getIdentifier());
        
        return new ResponseEntity<>(assetLinks, HttpStatus.OK);
    }

    public ResponseEntity<List<SpecificAssetID>> postAllAssetLinksById(@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier,@Parameter(in = ParameterIn.DEFAULT, description = "A list of specific Asset identifiers", required=true, schema=@Schema()) @Valid @RequestBody List<SpecificAssetID> body) {
        List<SpecificAssetID> assetIDs = aasDiscoveryService.createAllAssetLinksById(aasIdentifier.getIdentifier(), body);
        
        return new ResponseEntity<List<SpecificAssetID>>(assetIDs, HttpStatus.CREATED);
    }

}

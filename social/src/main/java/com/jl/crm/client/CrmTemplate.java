package com.jl.crm.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CrmTemplate extends AbstractOAuth2ApiBinding implements CrmOperations {

    private final File rootFile = new File(System.getProperty("java.io.tmpdir"));
    private URI apiBaseUri;

    private Map<String, String> mapOfExtensions = new ConcurrentHashMap<String, String>() {
        {
            put("jpeg", "jpg");
            put("jpg", "jpg");
            put("gif", "gif");
            put("png", "png");
        }
    };

    private Map<String, MediaType> mapOfMediaTypeExtenstions = new ConcurrentHashMap<String, MediaType>() {
        {
            put("png", MediaType.IMAGE_PNG);
            put("jpg", MediaType.IMAGE_JPEG);
            put("gif", MediaType.IMAGE_GIF);
        }
    };

    public CrmTemplate(String accessToken, String apiUrl) {
        super(accessToken);
        try {
            this.apiBaseUri = new URI(apiUrl);
            SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
            setRequestFactory(simpleClientHttpRequestFactory);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize the " + CrmTemplate.class.getName(), e);
        }
    }

    @Override
    public Collection<Customer> search(String token) {
        return null;
    }

    @Override
    public ProfilePhoto getUserProfilePhoto() {
        return null;
    }

    @Override
    public Customer updateCustomer(Long id, String firstName, String lastName) {
        return null;
    }

    @Override
    public void setProfilePhoto(byte[] bytesOfImage, MediaType mediaType) {

    }

    @Override
    public void removeCustomer(Long id) {

    }

    @Override
    public Collection<Customer> loadAllUserCustomers() {
        return null;
    }

    @Override
    public Customer createCustomer(String firstName, String lastName, Date signupDate) {
        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("firstName", firstName);
        customerMap.put("lastName", lastName);
        customerMap.put("signupDate", signupDate);

        ResponseEntity<Object> responseEntity = getRestTemplate().postForEntity(uriFrom("/customers"), customerMap, Object.class);

        URI uriOfNewCustomer = responseEntity.getHeaders().getLocation();
        URI uriOfUser = uriFrom("/users/" + currentUser().getId());

        // now we need to POST the URI of the user to the customer's /customers/$X/user property
        URI customerUserProperty = this.uriFrom("/customers/" + customer(uriOfNewCustomer).getId() + "/user");

        System.out.println("Customer user property: " + customerUserProperty.toString());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> updateRequest = new HttpEntity<String>(uriOfUser.toString(), httpHeaders);
        ResponseEntity<Map> response = this.getRestTemplate().postForEntity(customerUserProperty, updateRequest, Map.class);

        return null;
    }

    @Override
    public User user(Long id) {
        return getRestTemplate().getForEntity(uriFrom("/users/" + id), User.class).getBody();
    }

    @Override
    public Customer loadUserCustomer(Long id) {
        Long currentUser = this.currentUser().getId();
        URI uri = uriFrom("/users/" + currentUser + "/customers/" + id);
        return customer(uri);
    }

    @Override
    public User currentUser() {
        // /session/user
        ResponseEntity<Map<String, Object>> responseEntity = getRestTemplate().exchange(uriFrom("/session"), HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
        });
        Assert.isTrue(responseEntity.getStatusCode().equals(HttpStatus.OK), "invalid response status code");
        Map<String, Object> body = responseEntity.getBody();
        Number number = (Number) body.get("userId");
        long userId = number.longValue();
        return user(userId);
    }

    private Customer customer(URI uri) {
        ResponseEntity<Customer> customerResponseEntity = getRestTemplate().getForEntity(uri, Customer.class);
        return customerResponseEntity.getBody();
    }

    private URI uriFrom(String subUrl) {
        return this.uriFrom(subUrl, Collections.<String, String>emptyMap());
    }

    private URI uriFrom(String subUrl, Map<String, ?> params) {
        return UriComponentsBuilder.fromUri(this.apiBaseUri).path(subUrl).buildAndExpand(params).toUri();
    }

    @Override
    protected FormHttpMessageConverter getFormMessageConverter() {
        FormHttpMessageConverter formHttpMessageConverter = super.getFormMessageConverter();
        List<HttpMessageConverter<?>> partConverters;
        try {
            Field partConvertersField = field(FormHttpMessageConverter.class, "partConverters");
            partConverters = (List<HttpMessageConverter<?>>) partConvertersField.get(formHttpMessageConverter);
            ResourceHttpMessageConverter remove = null;
            for (HttpMessageConverter<?> hmc : partConverters) {
                if (hmc instanceof ResourceHttpMessageConverter) {
                    remove = (ResourceHttpMessageConverter) hmc;
                }
            }
            if (remove != null) {
                partConverters.remove(remove);
            }
            partConverters.add(new DefaultContentTypeGuessingResourceHttpMessageConverter());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return formHttpMessageConverter;
    }

    @Override
    protected List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(getFormMessageConverter());
        messageConverters.add(mappingJackson2HttpMessageConverter());
        messageConverters.add(getByteArrayMessageConverter());
        return messageConverters;
    }

    //Prefer this mapping message converter over the Jackson1 since Spring HATEOAS only requires Jackson 2 AFAICT
    protected MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

    @Override
    protected ByteArrayHttpMessageConverter getByteArrayMessageConverter() {
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_OCTET_STREAM, MediaType.IMAGE_JPEG, MediaType.IMAGE_GIF, MediaType.IMAGE_PNG
        ));
        return converter;
    }

    private static Field field(Class<?> cl, String fieldName) {
        Field field = null;
        try {
            field = cl.getDeclaredField(fieldName);
            if (field != null) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return field;
    }

    /**
     * The {@link ResourceHttpMessageConverter} ultimately defaults to using the
     * <A href= "http://www.oracle.com/technetwork/java/javase/downloads/index-135046.html">Java Activation Framework (JAF)</A> to guess
     * the mime type (content-type) of the uploaded image. Because JAF does not exist on Android, we instead use a few heuristics
     * to determine the mime type from the extension of known, relevant-to-our-application file types on Android.
     */
    public class DefaultContentTypeGuessingResourceHttpMessageConverter extends ResourceHttpMessageConverter {

        @Override
        protected MediaType getDefaultContentType(Resource resource) {
            try {
                MediaType ifAllElseFails = super.getDefaultContentType(resource);
                String fileName = resource.getFilename();
                int lastPeriod;
                if (fileName != null && (fileName = fileName.toLowerCase()) != null && ((lastPeriod = fileName.lastIndexOf(".")) != -1)) {
                    String ext = fileName.substring(lastPeriod + 1);
                    if (mapOfExtensions.containsKey(ext)) {
                        String canonicalExt = mapOfExtensions.get(ext);
                        return mapOfMediaTypeExtenstions.get(canonicalExt);
                    }
                }
                return ifAllElseFails;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
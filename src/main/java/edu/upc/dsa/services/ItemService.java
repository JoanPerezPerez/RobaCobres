package edu.upc.dsa.services;
import edu.upc.dsa.*;
import edu.upc.dsa.models.Item;
import edu.upc.dsa.models.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "/items", description = "Endpoint to Users Service")
@Path("/items")
public class ItemService {
    private ItemManager im;
    private StoreManager sm;
    private UserManager um;
    public ItemService() {
        this.im = ItemManagerImpl.getInstance();
        this.sm = StoreManagerImpl.getInstance();
        this.um = UserManagerImpl.getInstance();
        if (im.size()==0) {
            Item item1 = new Item("TrucoRumano");
            Item item2 = new Item("TrucoGitano");
            Item item3 = new Item("PelaCables2000");
            Item item4 = new Item("TrucoMurciano");
            this.im.addItem(item1);
            this.im.addItem(item2);
            this.im.addItem(item3);
            this.im.addItem(item4);
            this.sm.addAllItems(this.im.findAll());
            User u1 = new User("Blau", "Blau2002");
            User u2 = new User("Lluc", "Falco12");
            User u3 = new User("David", "1234");
            User u4 = new User("Marcel", "1234");
            this.um.addUser(u1);
            this.um.addUser(u2);
            this.um.addUser(u3);
            this.um.addUser(u4);
            this.sm.addAllUsers(this.um.findAll());
        }
    }
    @DELETE
    @ApiOperation(value = "delete an Item", notes = "hello")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "Item not found")
    })
    @Path("/{id}")
    public Response deleteItem(@PathParam("id") String id) {
        Item i = this.im.getItem(id);
        if (i == null) return Response.status(404).build();
        else this.im.deleteItem(id);
        return Response.status(201).build();
    }

    @GET
    @ApiOperation(value = "get all Items", notes = "hahaha")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Item.class, responseContainer="List"),
    })
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems() {

        List<Item> items = this.im.findAll();

        GenericEntity<List<Item>> entity = new GenericEntity<List<Item>>(items) {};
        return Response.status(201).entity(entity).build()  ;

    }

    @GET
    @ApiOperation(value = "get an Item", notes = "hahaha")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Item.class),
            @ApiResponse(code = 404, message = "Track not found")
    })
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItem(@PathParam("id") String id) {
        Item i = this.im.getItem(id);
        if (i == null) return Response.status(404).build();
        else  return Response.status(201).entity(i).build();
    }

    @POST
    @ApiOperation(value = "Add a new Item", notes = "hello")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response= Item.class),
            @ApiResponse(code = 500, message = "Validation Error")

    })
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response LoginUser(Item i) {

        if (i.getName()==null)  return Response.status(500).build();
        im.addItem(i);
        sm.addItem(i);
        return Response.status(201).entity(i).build();
    }

    @PUT
    @ApiOperation(value = "Aplica descuento/modifica Item", notes = "asdasd")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "Item not found")
    })
    @Path("/")
    public Response updateTrack(Item item) {

        Item i = this.im.updateItem(item);

        if (i == null) return Response.status(404).build();

        return Response.status(201).build();
    }
}

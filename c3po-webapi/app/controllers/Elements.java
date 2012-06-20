package controllers;

import helpers.Filter;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.elements;
import views.html.element;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MapReduceOutput;
import com.petpet.c3po.analysis.mapreduce.FilterValuesJob;
import com.petpet.c3po.api.dao.PersistenceLayer;
import com.petpet.c3po.common.Constants;
import com.petpet.c3po.datamodel.Element;
import com.petpet.c3po.datamodel.Property;
import com.petpet.c3po.utils.Configurator;
import com.petpet.c3po.utils.DataHelper;

public class Elements extends Controller {

  public static Result index() {
    final List<String> names = Application.getCollectionNames();
    
    return ok(elements.render(names, form(Filter.class), null, null));

  }

  public static Result list() {
    final Form<Filter> form = form(Filter.class).bindFromRequest();
    final Filter data = form.get();

    if (form.hasErrors()) {
      // return some error;
      return TODO;

    } else {
      // if everything is selected.
      Logger.debug("Form was complete: " + data.getCollection() + " " + data.getFilter() + " " + data.getValue() + " "
          + data.getValues().isEmpty());
      final List<String> values = getFilterValues(data);
      data.setValues(values);
      form.fill(data);
      return listElements(form);

    }
  }

  /**
   * Obtains the filter values for the selected filter and the current
   * collection. If the filter type equals none, then an empty list is returned.
   * 
   * @param data
   *          the data object holding the selection of the submitted form.
   * @return the list with possible values or an empty list.
   */
  private static List<String> getFilterValues(Filter data) {
    final List<String> result = new ArrayList<String>();

    if (data != null && data.getFilter() != null) {
      final PersistenceLayer p = Configurator.getDefaultConfigurator().getPersistence();
      FilterValuesJob job = null;

      if (!data.getFilter().equals("none")) {
        job = new FilterValuesJob(data.getCollection(), data.getFilter(), p);

        final MapReduceOutput output = job.execute();
        final List<BasicDBObject> jobresults = (List<BasicDBObject>) output.getCommandResult().get("results");

        for (final BasicDBObject dbo : jobresults) {
          result.add(dbo.getString("_id"));
        }
      }
    }
    return result;
  }

  public static Result show(String id) {
    Logger.info("Select element with id: " + id);
    final List<String> names = Application.getCollectionNames();
    final PersistenceLayer pl = Configurator.getDefaultConfigurator().getPersistence();
    DBCursor cursor = pl.find(Constants.TBL_ELEMENTS, new BasicDBObject("_id", new ObjectId(id)));

    if (cursor.count() == 0) {
      Logger.info("Cursor selected " + cursor.count());
      return notFound("No such element exists in the db.");
    } else if (cursor.count() > 1) {
      Logger.info("Cursor selected " + cursor.count());
      return notFound("One or more objects with this id exist");
    } else {

      Element elmnt = DataHelper.parseElement(cursor.next(), pl);

      return ok(
       element.render(elmnt)
      );
    }

  }

  public static Result listElements(Form<Filter> form) {
    final Filter data = form.get();
    final List<Element> result = new ArrayList<Element>();
    final List<String> names = Application.getCollectionNames();
    final PersistenceLayer pl = Configurator.getDefaultConfigurator().getPersistence();
    final Property f = pl.getCache().getProperty(data.getFilter());

    Logger.info("List objects in collection: " + data.getCollection() + " with offset: " + data.getOffset());

    final BasicDBObject query = new BasicDBObject();
    query.put("collection", data.getCollection());

    if (!data.getFilter().equals("none")) {
      query.put("metadata." + f.getId() + ".value", data.getValue());
    }

    final DBCursor cursor = pl.getDB().getCollection(Constants.TBL_ELEMENTS).find(query).skip(data.getOffset())
        .limit(data.getBatch());

    Logger.info("Cursor has: " + cursor.count() + " objects");

    while (cursor.hasNext()) {
      final Element e = DataHelper.parseElement(cursor.next(), pl);

      if (e.getName() == null) {
        e.setName("missing name");
      }

      result.add(e);
    }

    Logger.info("Values are empty: " + data.getValues().isEmpty());
    return ok(elements.render(names, form, data.getValues(), result));

  }
}
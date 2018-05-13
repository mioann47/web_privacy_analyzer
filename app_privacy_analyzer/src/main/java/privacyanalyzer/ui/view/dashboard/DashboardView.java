package privacyanalyzer.ui.view.dashboard;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.Marker;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsLine;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.board.Row;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;

import privacyanalyzer.backend.data.DashboardData;
import privacyanalyzer.backend.data.DeliveryStats;
import privacyanalyzer.backend.data.entity.Order;
import privacyanalyzer.backend.data.entity.Permission;
import privacyanalyzer.backend.data.entity.Product;
import privacyanalyzer.backend.service.ApkService;
import privacyanalyzer.backend.service.OrderService;
import privacyanalyzer.backend.service.PermissionService;
import privacyanalyzer.ui.components.OrdersGrid;
import privacyanalyzer.ui.navigation.NavigationManager;
import privacyanalyzer.ui.view.orderedit.OrderEditView;

/**
 * The dashboard view showing statistics about sales and deliveries.
 * <p>
 * Created as a single View class because the logic is so simple that using a
 * pattern like MVP would add much overhead for little gain. If more complexity
 * is added to the class, you should consider splitting out a presenter.
 */
@SpringView
public class DashboardView extends DashboardViewDesign implements View {

	private static final String DELIVERIES = "Deliveries";

	private static final String BOARD_ROW_PANELS = "board-row-panels";

	private final NavigationManager navigationManager;
	private final OrderService orderService;

	private final BoardLabel totalAPKlabel = new BoardLabel("APKs analyzed", "3/7", "today");
	private final BoardLabel DangerousAPKslabel = new BoardLabel("Dangerous APKs found", "1", "na");
	private final BoardBox notAvailableBox = new BoardBox(DangerousAPKslabel);
	private final BoardLabel addedTodayLabel = new BoardLabel("Added Today", "2", "new");
	//private final BoardLabel tomorrowLabel = new BoardLabel("Tomorrow", "4", "tomorrow");
	private final BoardLabel DangerousPermissionlabel = new BoardLabel("Most Used Dangerous Permission", "1", "na");
	//private final Chart deliveriesThisMonthGraph = new Chart(ChartType.COLUMN);
	private final Chart deliveriesThisYearGraph = new Chart(ChartType.COLUMN);
	private final Chart yearlySalesGraph = new Chart(ChartType.AREA);
	private final Chart monthlyProductSplit = new Chart(ChartType.PIE);
	private final OrdersGrid dueGrid;

	private final Chart permissionPieChart= new Chart(ChartType.PIE);
	
	public NativeSelect<Permission> select=new NativeSelect<Permission>();
	private ListSeries deliveriesThisMonthSeries;
	private ListSeries deliveriesThisYearSeries;
	private ListSeries[] salesPerYear;

	private DataSeries deliveriesPerProductSeries;

	private final ApkService apkService;
	private final PermissionService permissionService;
	@Autowired
	public DashboardView(PermissionService permissionService,ApkService apkService,NavigationManager navigationManager, OrderService orderService, OrdersGrid dueGrid) {
		this.navigationManager = navigationManager;
		this.orderService = orderService;
		this.dueGrid = dueGrid;
		this.apkService=apkService;
		this.permissionService=permissionService;
	}

	

	
	private Long convertToLong(Object o){
        String stringToConvert = String.valueOf(o);
        Long convertedLong = Long.parseLong(stringToConvert);
        return convertedLong;

    }
	Component bar;
	@PostConstruct
	public void init() {
		select.clear();
		select.setItems(permissionService.getPermissionRepository().findAll());
		select.setEmptySelectionAllowed(false);
		// Show 5 items and a scrollbar if there are more
		select.setVisibleItemCount(8);
		select.setSizeFull();
		select.setSelectedItem(permissionService.getPermissionRepository().findAll().get(0));
		select.addValueChangeListener(event -> {
		    Optional<Permission> selected =  select.getSelectedItem();
		    
		    List<Object[]> r=permissionService.getApkPermissionAssociationRepository().getPermissionIdentifications(selected.get());
		    refreshPie(r);
		   /* System.out.println("Permission: "+selected.get());
		    //initProductSplitMonthlyGraph(r);
		    for (Object[] temp:r) {
		    	
		    		deliveriesPerProductSeries.add(new DataSeriesItem((String) temp[0], Integer.parseInt((String.valueOf(temp[1]) ))));
		    		System.out.println("perm: "+(String) temp[0]+"int: "+Integer.parseInt((String.valueOf(temp[1]) )));
		    	
		    	
		    }
		    //Notification.show(selected.get() + " items.");
		    
		    for (DataSeriesItem d:deliveriesPerProductSeries.getData()) {
		    	
		    	System.out.println(d.toString());
		    }*/
		});
		bar =new BasicBar(permissionService.getApkPermissionAssociationRepository().findTopUsedPermissions(new PageRequest(0, 5))).getChart();
		
		for (Object[] p:permissionService.getApkPermissionAssociationRepository().findTopUsedPermissions(new PageRequest(0, 5))) {
			System.out.println(((Permission) p[0]).getPermissionName()+" found: "+convertToLong(p[1]));
		}
		setResponsive(true);

		Row row = board.addRow(new BoardBox(totalAPKlabel), notAvailableBox, new BoardBox(addedTodayLabel)
				/*,new BoardBox(tomorrowLabel)*/);
		row.addStyleName("board-row-group");

		row = board.addRow(/*new BoardBox(bar),*/ new BoardBox(DangerousPermissionlabel));
		row.addStyleName("board-row-group");

		row = board.addRow(new BoardBox(bar));
		row.addStyleName(BOARD_ROW_PANELS);

		row = board.addRow(new BoardBox(permissionPieChart), new BoardBox(select));
		row.addStyleName(BOARD_ROW_PANELS);
		initProductSplitMonthlyGraph();
		initTop10UsedPermissions();
		initYearlySalesGraph();
		initMyChart();
		dueGrid.setId("dueGrid");
		dueGrid.setSizeFull();
		refreshPie(permissionService.getApkPermissionAssociationRepository().getPermissionIdentifications(permissionService.getPermissionRepository().findAll().get(0)));
		
		dueGrid.addSelectionListener(e -> selectedOrder(e.getFirstSelectedItem().get()));
	}

	private void initYearlySalesGraph() {
		yearlySalesGraph.setId("yearlySales");
		yearlySalesGraph.setSizeFull();
		int year = Year.now().getValue();

		Configuration conf = yearlySalesGraph.getConfiguration();
		conf.setTitle("Sales last years");
		conf.getxAxis().setCategories(getMonthNames());
		conf.getChart().setMarginBottom(6);

		PlotOptionsLine options = new PlotOptionsLine();
		options.setMarker(new Marker(false));
		options.setShadow(true);
		conf.setPlotOptions(options);

		salesPerYear = new ListSeries[3];
		for (int i = 0; i < salesPerYear.length; i++) {
			salesPerYear[i] = new ListSeries(Integer.toString(year - i));
			salesPerYear[i].setPlotOptions(new PlotOptionsLineWithZIndex(year - i));
			conf.addSeries(salesPerYear[i]);
		}
		conf.getyAxis().setTitle("");

	}
	
	
	private void refreshPie(List<Object[]> data) {
		pieData= new DataSeries("Type");
		for (Object[] temp:data) {
	    	
			pieData.add(new DataSeriesItem((String) temp[0], Integer.parseInt((String.valueOf(temp[1]) ))));
    		System.out.println("perm: "+(String) temp[0]+"int: "+Integer.parseInt((String.valueOf(temp[1]) )));
    	
    	
    }
		permissionPieChart.getConfiguration().setSeries(pieData);
		permissionPieChart.drawChart();
	}
	
	public DataSeries pieData;
	private void initMyChart() {
		permissionPieChart.setId("permissionPie");
		permissionPieChart.setSizeFull();
		
		Configuration conf= permissionPieChart.getConfiguration();
		conf.setTitle("Permission Identification Usage");
		pieData=new DataSeries("Used");
		conf.addSeries(pieData);
		conf.getyAxis().setTitle("");
		
	}
	private void initProductSplitMonthlyGraph() {
		monthlyProductSplit.setId("monthlyProductSplit");
		monthlyProductSplit.setSizeFull();
		
		
		LocalDate today = LocalDate.now();

		Configuration conf = monthlyProductSplit.getConfiguration();
		String thisMonth = today.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
		conf.setTitle("Products delivered in " + thisMonth);
		deliveriesPerProductSeries = new DataSeries(DELIVERIES);
		
		
		conf.addSeries(deliveriesPerProductSeries);
		conf.getyAxis().setTitle("");

	}

	private void initTop10UsedPermissions() {
		LocalDate today = LocalDate.now();

		//deliveriesThisMonthGraph.setId("deliveriesThisMonth");
		//deliveriesThisMonthGraph.setSizeFull();

		deliveriesThisYearGraph.setId("deliveriesThisYear");
		deliveriesThisYearGraph.setSizeFull();

		Configuration yearConf = deliveriesThisYearGraph.getConfiguration();

		yearConf.setTitle("Deliveries in " + today.getYear());
		yearConf.getChart().setMarginBottom(6);
		yearConf.getxAxis().setCategories(getMonthNames());
		yearConf.getxAxis().setLabels(new Labels(null));
		yearConf.getLegend().setEnabled(false);
		deliveriesThisYearSeries = new ListSeries(DELIVERIES);
		yearConf.addSeries(deliveriesThisYearSeries);
		configureColumnSeries(deliveriesThisYearSeries);

		/*Configuration monthConf = deliveriesThisMonthGraph.getConfiguration();
		String thisMonth = today.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
		monthConf.setTitle("Deliveries in " + thisMonth);
		monthConf.getChart().setMarginBottom(6);
		monthConf.getLegend().setEnabled(false);
		deliveriesThisMonthSeries = new ListSeries(DELIVERIES);
		monthConf.addSeries(deliveriesThisMonthSeries);
		configureColumnSeries(deliveriesThisMonthSeries);

		int daysInMonth = YearMonth.of(today.getYear(), today.getMonthValue()).lengthOfMonth();
		String[] categories = IntStream.rangeClosed(1, daysInMonth).mapToObj(Integer::toString)
				.toArray(size -> new String[size]);
		monthConf.getxAxis().setCategories(categories);
		monthConf.getxAxis().setLabels(new Labels(false));*/
	}

	protected void configureColumnSeries(ListSeries series) {
		PlotOptionsColumn options = new PlotOptionsColumn();
		options.setBorderWidth(1);
		options.setGroupPadding(0);
		series.setPlotOptions(options);

		YAxis yaxis = series.getConfiguration().getyAxis();
		yaxis.setGridLineWidth(0);
		yaxis.setLabels(new Labels(false));
		yaxis.setTitle("");
	}

	private String[] getMonthNames() {
		return Stream.of(Month.values()).map(month -> month.getDisplayName(TextStyle.SHORT, Locale.US))
				.toArray(size -> new String[size]);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		DashboardData data = fetchData();
		updateLabels(data.getDeliveryStats());
		updateGraphs(data);
	}

	private DashboardData fetchData() {
		return orderService.getDashboardData(MonthDay.now().getMonthValue(), Year.now().getValue());
	}

	private void updateGraphs(DashboardData data) {
		//deliveriesThisMonthSeries.setData(data.getDeliveriesThisMonth());
		deliveriesThisYearSeries.setData(data.getDeliveriesThisYear());

		for (int i = 0; i < 3; i++) {
			salesPerYear[i].setData(data.getSalesPerMonth(i));
		}

		for (Entry<Product, Integer> entry : data.getProductDeliveries().entrySet()) {
			deliveriesPerProductSeries.add(new DataSeriesItem(entry.getKey().getName(), entry.getValue()));
		}
	}

	private void updateLabels(DeliveryStats deliveryStats) {
		totalAPKlabel.setContent(Long.toString(apkService.getRepository().count()));
		DangerousAPKslabel.setContent(Long.toString(apkService.getRepository().findDangerousApk()));
		notAvailableBox.setNeedsAttention(deliveryStats.getNotAvailableToday() > 0);
		addedTodayLabel.setContent(Integer.toString(apkService.getRepository().findTodayAddedAPKs().size()));
		//tomorrowLabel.setContent(Integer.toString(deliveryStats.getDueTomorrow()));
		System.out.println();
		Permission p=permissionService.getApkPermissionAssociationRepository().findTopUsedDangerousPermissions(new PageRequest(0, 1)).get(0);
		DangerousPermissionlabel.setContent(p.getPermissionName());
	}

	/**
	 * Extends {@link PlotOptionsLine} to support zIndex. Omits getter/setter,
	 * since they are not needed in our case.
	 *
	 */
	private static class PlotOptionsLineWithZIndex extends PlotOptionsLine {
		@SuppressWarnings("unused")
		private Number zIndex;

		public PlotOptionsLineWithZIndex(Number zIndex) {
			this.zIndex = zIndex;
		};
	}

	public void selectedOrder(Order order) {
		navigationManager.navigateTo(OrderEditView.class, order.getId());
	}

}

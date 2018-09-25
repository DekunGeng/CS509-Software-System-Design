package com.controller;

import com.config.GlobalConfiguration;
import com.model.AvailDayPO;
import com.model.CalendarModel;
import com.model.CalendarPO;
import com.model.MeetingPO;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static com.config.GlobalConfiguration.FORMATTER;

public class MainController implements Initializable {

    @FXML
    private ChoiceBox<String> duration;

    @FXML
    private ListView<CalendarPO> cals;

    @FXML
    private ListView<AvailDayPO> availDay;

    @FXML
    private ListView<MeetingPO> meetingList;

    @FXML
    private Text durationText;

    @FXML
    private DatePicker startDay;

    @FXML
    private Text fromTimeText;

    @FXML
    private DatePicker addDayTo;

    @FXML
    private Text nameText;

    @FXML
    private TextField meetingToTime;

    @FXML
    private DatePicker endDay;

    @FXML
    private TextField name;

    @FXML
    private TextField fromTime;

    @FXML
    private DatePicker addDayFrom;

    @FXML
    private DatePicker meetingDay;

    @FXML
    private Text endTimeText;

    @FXML
    private TextField endTime;

    @FXML
    private TextField meetingFromTime;

    @FXML
    private TextField hoster;

    @FXML
    private Text organizerText;

    @FXML
    private Text meetingDayText;

    @FXML
    private Text meetingStartTimeText;

    @FXML
    private Text meetingEndTimeText;

    @FXML
    private DatePicker chooseDay;

    private CalendarPO curCalendar = null;

    private MeetingPO curMeeting = null;

    private Date byDate;

    private final static String[] durations = new String[]{"No Defined", "10min", "15min", "20min", "30min", "60min"};
    private final static int[] add_durations = new int[]{0, 10, 15, 20, 30, 60};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        duration.getItems().addAll(durations);
        duration.getSelectionModel().selectFirst();
        cals.setCellFactory(it -> {
            ListCell<CalendarPO> cell = new ListCell<CalendarPO>() {
                @Override
                protected void updateItem(CalendarPO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.getName());
                    }
                }
            };

            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem();
            deleteItem.textProperty().bind(Bindings.format("Delete", cell.itemProperty()));
            deleteItem.setOnAction(event -> {
                CalendarPO po = cell.getItem();
                it.getItems().remove(po);
                curCalendar = null;
                curMeeting = null;
                CalendarModel.calendarPOMap.remove(po.getName());
                meetingList.setItems(FXCollections.emptyObservableList());
                clearMeeting();
                byDate = null;
                CalendarModel.save();
            });
            contextMenu.getItems().addAll(deleteItem);
            // cell.textProperty().bind(cell.itemProperty().asString());
            cell.emptyProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });

            return cell;
        });
        cals.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            curCalendar = newValue;
            showCalendar(newValue);
            curMeeting = null;
            clearMeeting();
            byDate = null;
            meetingList.setItems(FXCollections.emptyObservableList());
        });
        availDay.setCellFactory(it -> {
            ListCell<AvailDayPO> cell = new ListCell<AvailDayPO>() {
                @Override
                protected void updateItem(AvailDayPO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(FORMATTER.format(item.getStartDay()) + " to " + FORMATTER.format(item.getEndDay()));
                    }
                }
            };
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem();
            deleteItem.textProperty().bind(Bindings.format("Delete", cell.itemProperty()));
            deleteItem.setOnAction(event -> {
                AvailDayPO po = cell.getItem();
                it.getItems().remove(po);
                curCalendar.getAvailDays().remove(po);
                // delete related meeting
                List<MeetingPO> meetings = curCalendar.getMeeting();
                for (MeetingPO meeting : meetings) {
                    if (!po.getStartDay().after(meeting.getDay()) && !po.getEndDay().before(meeting.getDay())) {
                        meetings.remove(meeting);
                    }
                }
                CalendarModel.save();
            });
            contextMenu.getItems().addAll(deleteItem);
            // cell.textProperty().bind(cell.itemProperty().asString());
            cell.emptyProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });

        meetingList.setCellFactory(it -> new ListCell<MeetingPO>() {
            @Override
            protected void updateItem(MeetingPO item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getBeginTime() + " to " + item.getEndTime());
                }
            }
        });

        meetingList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            curMeeting = newValue;
            showMeeting(newValue);
        });

        chooseDay.setOnAction(it -> {
            if (curCalendar != null) {
                Date cur = Date.from(chooseDay.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                byDate = cur;
                List<MeetingPO> days = new ArrayList<>();
                for (MeetingPO po : curCalendar.getMeeting()) {
                    if (po.getDay().equals(cur)) {
                        days.add(po);
                    }
                }
                meetingList.setItems(FXCollections.observableList(days));
            }
        });

        loadData();
    }

    private void clearMeeting() {
        organizerText.setText("");
        meetingStartTimeText.setText("");
        meetingEndTimeText.setText("");
        meetingDayText.setText("");
    }

    private void showMeeting(MeetingPO newValue) {
        organizerText.setText(newValue.getOrganization());
        meetingStartTimeText.setText(newValue.getBeginTime());
        meetingEndTimeText.setText(newValue.getEndTime());
        meetingDayText.setText(FORMATTER.format(newValue.getDay()));
    }

    @FXML
    void onCreate(ActionEvent event) {
        LocalDate startDayValue = startDay.getValue();
        LocalDate endDayValue = endDay.getValue();
        String from = fromTime.getText().replaceAll("\\s+", "");
        String end = endTime.getText().replaceAll("\\s+", "");
        String cal = name.getText().replaceAll("\\s+", "");
        int idx = duration.getSelectionModel().getSelectedIndex();
        Date fromTime;
        Date endTime;
        try {
            fromTime = GlobalConfiguration.TIME_FORMATTER.parse(from);
            endTime = GlobalConfiguration.TIME_FORMATTER.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
            alertError("Error Create Calendar!");
            return;
        }

        if (startDayValue.isAfter(endDayValue) || from.length() == 0 || end.length() == 0
                || fromTime.after(endTime) || cal.length() == 0) {
            alertError("Illegal Arguments!");
            return;
        }
        CalendarPO po = new CalendarPO();
        po.setName(cal);
        List<AvailDayPO> availDayPOS = new ArrayList<>();
        availDayPOS.add(new AvailDayPO(Date.from(startDayValue.atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(endDayValue.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        po.setAvailDays(availDayPOS);
        po.setMeeting(new ArrayList<>());
        po.setStartTime(from);
        po.setEndTime(end);
        po.setDurationOption(idx);
        if (CalendarModel.insertCalendar(po)) {
            alertInfo("Create Successfully");
            loadData();
        } else {
            alertError("Fail to create calendar");
        }
    }

    @FXML
    void onAddDay(ActionEvent event) {
        if (curCalendar != null) {
            Date startDayValue = Date.from(addDayFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDayValue = Date.from(addDayTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            if (!startDayValue.after(endDayValue)) {
                List<AvailDayPO> already = curCalendar.getAvailDays();
                for (AvailDayPO po : already) {
                    if ((!startDayValue.after(po.getEndDay()) && !startDayValue.before(po.getStartDay())) ||
                            (!endDayValue.after(po.getEndDay()) && !endDayValue.before(po.getStartDay()))) {
                        alertError("Already Defined");
                        return;
                    }
                }
                AvailDayPO newDay = new AvailDayPO(startDayValue, endDayValue);
                already.add(newDay);
                availDay.setItems(FXCollections.observableList(already));
                CalendarModel.save();
                alertInfo("Add day successfully!");
            } else {
                alertError("Illegal Arguments!");
            }
        } else {
            alertError("Please select calendar");
        }
    }

    @FXML
    public void onAddMeeting(ActionEvent event) {
        if (curCalendar != null) {
            Date startDayValue = Date.from(meetingDay.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<AvailDayPO> already = curCalendar.getAvailDays();
            boolean inArea = false;
            for (AvailDayPO po : already) {
                if (!startDayValue.after(po.getEndDay()) && !startDayValue.before(po.getStartDay())) {
                    inArea = true;
                    break;
                }
            }
            if (!inArea) {
                alertError("Day not available");
                return;
            }
            String begin = meetingFromTime.getText().replaceAll("\\s+", "");
            String end = null;
            if (curCalendar.getDurationOption() == 0) {
                end = meetingToTime.getText().replaceAll("\\s+", "");
            }
            String organizer = hoster.getText().trim();
            if (begin.isEmpty() || (end != null && end.isEmpty()) || organizer.isEmpty()) {
                alertError("Input cannot be empty");
                return;
            }

            try {
                Date fromTime = GlobalConfiguration.TIME_FORMATTER.parse(begin);
                Date endTime;
                if (end == null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(fromTime);
                    calendar.add(Calendar.MINUTE, add_durations[curCalendar.getDurationOption()]);
                    endTime = calendar.getTime();
                } else {
                    endTime = GlobalConfiguration.TIME_FORMATTER.parse(end);
                }
                if (!fromTime.before(endTime) || begin.compareTo(curCalendar.getStartTime()) < 0 || end.compareTo(curCalendar.getEndTime()) > 0) {
                    alertError("Illegal time");
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                alertError("Error Create Meeting!");
                return;
            }

            List<MeetingPO> meetings = curCalendar.getMeeting();
            for (MeetingPO each : meetings) {
                if (each.getDay().equals(startDayValue)) {
                    if ((begin.compareTo(each.getBeginTime()) >= 0 && begin.compareTo(each.getEndTime()) < 0) ||
                            (end.compareTo(each.getBeginTime()) > 0 && end.compareTo(each.getEndTime()) <= 0)) {
                        alertError("This period is ordered already");
                        return;
                    }
                }
            }
            MeetingPO it = new MeetingPO(startDayValue, begin, end, organizer);
            meetings.add(it);
            CalendarModel.save();
            if (byDate == null) {
                meetingList.setItems(FXCollections.observableList(curCalendar.getMeeting()));
            } else {
                if (byDate.equals(startDayValue)) {
                    meetingList.getItems().add(it);
                }
            }
            alertInfo("Add meeting successfully!");
        } else {
            alertError("Please select calendar");
        }
    }

    @FXML
    public void onCancel(ActionEvent event) {
        if (curMeeting != null && curCalendar != null) {
            curCalendar.getMeeting().remove(curMeeting);
            CalendarModel.save();
            clearMeeting();
            meetingList.getItems().remove(curMeeting);
            curMeeting = null;
        } else {
            alertError("Please select meeting");
        }
    }

    @FXML
    public void onShowAll(ActionEvent event) {
        if (curCalendar != null) {
            byDate = null;
            meetingList.setItems(FXCollections.observableList(curCalendar.getMeeting()));
        } else {
            alertError("Please select calendar");
        }
    }

    private void loadData() {
        List<CalendarPO> calendarPOS = new ArrayList<>(CalendarModel.calendarPOMap.values());
        calendarPOS.sort(Comparator.comparing(CalendarPO::getName));
        cals.setItems(FXCollections.observableList(calendarPOS));
    }

    private void showCalendar(CalendarPO cal) {
        nameText.setText(cal.getName());
        availDay.setItems(FXCollections.observableList(cal.getAvailDays()));
        fromTimeText.setText(cal.getStartTime());
        endTimeText.setText(cal.getEndTime());
        durationText.setText(durations[cal.getDurationOption()]);
        meetingToTime.setVisible(cal.getDurationOption() == 0);
    }

    private void alertError(String err) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(null);
        alert.setContentText(err);
        alert.showAndWait();
    }

    private void alertInfo(String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }

}

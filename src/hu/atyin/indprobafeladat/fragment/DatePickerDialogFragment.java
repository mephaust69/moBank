package hu.atyin.indprobafeladat.fragment;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

/**
 * 
 * D�tumv�laszt� Dial�gus
 *
 */
public class DatePickerDialogFragment extends SherlockDialogFragment {
	
	public static final String TAG = "DatePickerDialogFragment";
	
	// Kiv�lasztott d�tum
	private Calendar calSelectedDate = Calendar.getInstance();
	// Melyik d�tum (from vagy To)
	private int which;
	
	// listener
	private IDatePickerDialogFragment listener;
	
	/**
	 * P�ld�nyos�t�s
	 * 
	 * @param which
	 * @param selected
	 * @return
	 */
	public static DatePickerDialogFragment newInstance(int which, Calendar selected) {
		DatePickerDialogFragment result = new DatePickerDialogFragment();
		
		Bundle b = new Bundle();
		
		b.putInt("which", which);
		b.putSerializable("selected", selected);
		result.setArguments(b);
		
		return result;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		if(getTargetFragment() != null) {
			try {
				listener = (IDatePickerDialogFragment) getTargetFragment();
			}
			catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getArguments() != null) {
			which = getArguments().getInt("which");
			calSelectedDate = (Calendar) getArguments().getSerializable("selected");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Megnyit�skor d�tum be�ll�t�sa legut�bbira
		return new DatePickerDialog(getSherlockActivity(), mDateSetListener, 
				calSelectedDate.get(Calendar.YEAR), 
				calSelectedDate.get(Calendar.MONTH), 
				calSelectedDate.get(Calendar.DAY_OF_MONTH));
	}
	
	// Kiv�lasztott d�tum be�ll�t�sa
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			calSelectedDate.set(Calendar.YEAR, year);
			calSelectedDate.set(Calendar.MONTH, monthOfYear);
			calSelectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			
			if(listener != null) {
				listener.onDateSelected(calSelectedDate, buildDateString(), which);
			}
			
			dismiss();
		}
	};
	
	/**
	 * D�tum String fel�p�t�se
	 */
	private String buildDateString() {
		StringBuilder dateString = new StringBuilder();
		
		dateString.append(calSelectedDate.get(Calendar.YEAR));
		dateString.append("-");
		
		if((calSelectedDate.get(Calendar.MONTH)+1) < 10)
			dateString.append("0" + (calSelectedDate.get(Calendar.MONTH)+1));
		else
			dateString.append(calSelectedDate.get(Calendar.MONTH)+1);
		
		dateString.append("-");
		
		if(calSelectedDate.get(Calendar.DAY_OF_MONTH) < 10)
			dateString.append("0" + calSelectedDate.get(Calendar.DAY_OF_MONTH));
		else
			dateString.append(calSelectedDate.get(Calendar.DAY_OF_MONTH));
		
		return dateString.toString();
	}
	
	// listener interface
	public interface IDatePickerDialogFragment {
		public void onDateSelected(Calendar selected, String date, int which);
	}
}

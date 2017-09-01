package com.zhongjiaxin.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zhaokaiwen on 2017/8/23.
 */

public class CrimeFragment extends Fragment {
    private static String ARG_CRIME_ID = "crime_id";
    private static String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_CONTACT = 1;//用于读取联系人

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);//通知fragment调用onCreateOptionsMenu()方法创建菜单栏  否则无效果
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getmTitle());

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setmTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePackerFragment dialog = DatePackerFragment.newInstance(mCrime.getmDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CODE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.ismSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setmSolved(b);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                i = Intent.createChooser(i,getString(R.string.send_report));//必须选择分享方式
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
//        pickContact.addCategory(Intent.CATEGORY_HOME);//删除故意设置类别测试 是否有联系人代码
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if ((mCrime.getmSuspect() != null)) {

            mSuspectButton.setText(mCrime.getmSuspect());
        }

        //检查手机里是否有联系人应用
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {//没有应用就设置按钮为不可用
            mSuspectButton.setEnabled(false);
        }

        return v;
//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                CrimeLab.get(getActivity()).delCrime(mCrime);
                Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;


        if (requestCode == REQUEST_CODE) {
            Date date = (Date) data.getSerializableExtra(DatePackerFragment.EXTRA_DATE);
            mCrime.setmDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {

                if (c.getCount() == 0) {
                    return;
                }

                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setmSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }


        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getmDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.ismSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString((R.string.crime_report_unsolved));
        }
        String dateFormat = "EEE,MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getmDate()).toString();

        String suspect = mCrime.getmSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getmTitle(), dateString, solvedString, suspect);
        return report;
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }
}
